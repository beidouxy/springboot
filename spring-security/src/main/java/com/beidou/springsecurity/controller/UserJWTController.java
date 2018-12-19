package com.beidou.springsecurity.controller;

import com.alibaba.fastjson.JSONObject;
import com.beidou.springsecurity.bean.LoginBean;
import com.beidou.springsecurity.bean.SysUserBean;
import com.beidou.springsecurity.config.KaptchaConfig;
import com.beidou.springsecurity.security.SecurityUtils;
import com.beidou.springsecurity.security.jwt.JWTConfigurer;
import com.beidou.springsecurity.security.jwt.TokenProvider;
import com.beidou.springsecurity.service.SysUserService;
import com.beidou.springsecurity.utils.Constants;
import com.beidou.springsecurity.utils.MD5Utils;
import com.beidou.springsecurity.utils.redis.RedisKeys;
import com.beidou.springsecurity.utils.redis.RedisUtils;
import com.google.code.kaptcha.impl.DefaultKaptcha;
import com.centling.utils.Result;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.imageio.ImageIO;
import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.*;

@RestController
@RequestMapping("/api")
public class UserJWTController {

    private Logger logger = LoggerFactory.getLogger(UserJWTController.class);

    @Autowired
    private TokenProvider tokenProvider;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private SysUserService sysUserService;

    @Autowired
    private RedisUtils redisUtils;

    @Autowired
    private KaptchaConfig kaptchaConfig;

    @Value(value = "${spring.redis.open}")
    private boolean redisOpenStatus;
    @Value(value = "${Login.singleLogin}")
    private boolean singleLoginStatus;
    @Value(value = "${application.security.authentication.jwt.tokenValidityInSeconds}")
    private long tokenValidityInSeconds;

    @Value(value = "${Login.retryCounts}")
    private Integer retryCounts;
    @Value(value = "${Login.expireTime}")
    private int expireTime;
    @Value(value = "${Login.initialPassword}")
    private String initialPassword;


    @RequestMapping(value = "/authenticate", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> authorize(@Valid @RequestBody LoginBean loginBean, HttpServletResponse response) {

        UsernamePasswordAuthenticationToken authenticationToken =
                new UsernamePasswordAuthenticationToken(loginBean.getUsername(), loginBean.getPassword());

        try {
            Authentication authentication = authenticationManager.authenticate(authenticationToken);
            SecurityContextHolder.getContext().setAuthentication(authentication);
            boolean rememberMe = (loginBean.isRememberMe() == null) ? false : loginBean.isRememberMe();
            String jwt = tokenProvider.createToken(authentication, rememberMe);
            response.addHeader(JWTConfigurer.AUTHORIZATION_HEADER, "Bearer " + jwt);
            response.addHeader(JWTConfigurer.ACCESS_CONTROL_EXPOSE_HEAEDERS, "Authorization");

            return ResponseEntity.ok(new JWTToken(jwt));
        } catch (AuthenticationException exception) {
            exception.printStackTrace();
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }
    }

    @RequestMapping(value = "/authenticate/appLogin", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Result> appLogin(@Valid @RequestBody LoginBean loginBean, HttpServletResponse response, HttpServletRequest request) {
        String retryRedisKey = "loginKey:" + loginBean.getUsername();
        Integer time = redisUtils.get(retryRedisKey, Integer.class);
        if (time != null) {
            if (time >= 4) {
                return new ResponseEntity<Result>(new Result(401, "失败次数过多，已被锁定，请稍后重试"), HttpStatus.OK);
            }
        }
        if (redisOpenStatus) {
            if (loginBean.getRedisKey() == null) {
                return new ResponseEntity<Result>(new Result(400, "缺少参数"), HttpStatus.OK);
            }
            String kaptchat = redisUtils.get(loginBean.getRedisKey());
            if (!(loginBean.getKaptcha().equalsIgnoreCase(kaptchat))) {
                return new ResponseEntity<Result>(new Result(400, "验证码不正确"), HttpStatus.OK);
            } else {
                redisUtils.delete(loginBean.getRedisKey());
            }
        }
        UsernamePasswordAuthenticationToken authenticationToken =
                new UsernamePasswordAuthenticationToken(loginBean.getUsername(), MD5Utils.parseStrToMd5U32(loginBean.getPassword()));

        try {
            AppLoginReturnDTO appLoginReturnDTO = login(authenticationToken, loginBean, response);
            return new ResponseEntity<Result>(new Result(200, "登录成功", appLoginReturnDTO), HttpStatus.OK);

        } catch (AuthenticationException exception) {
            if (time == null) {
                redisUtils.set(retryRedisKey, 1, expireTime);
                return new ResponseEntity<Result>(new Result(401, "用户名或密码错误，您还有" + (retryCounts - 1) + "次机会"), HttpStatus.OK);
            } else {
                Integer retryLimitCounts = time + 1;
                redisUtils.set(retryRedisKey, retryLimitCounts, expireTime);
                return new ResponseEntity<Result>(new Result(401, "用户名或密码错误，您还有" + (retryCounts - retryLimitCounts) + "次机会"), HttpStatus.OK);
            }
        }
    }

    private AppLoginReturnDTO login(UsernamePasswordAuthenticationToken authenticationToken, LoginBean loginBean, HttpServletResponse response) {
        Authentication authentication = this.authenticationManager.authenticate(authenticationToken);
        SecurityContextHolder.getContext().setAuthentication(authentication);
        boolean rememberMe = (loginBean.isRememberMe() == null) ? false : loginBean.isRememberMe();
        String jwt = tokenProvider.createToken(authentication, rememberMe);
        response.addHeader(JWTConfigurer.AUTHORIZATION_HEADER, "Bearer " + jwt);
        response.addHeader(JWTConfigurer.ACCESS_CONTROL_EXPOSE_HEAEDERS, "Authorization");

        Long currentUserId = SecurityUtils.getCurrentUserId();
        if (singleLoginStatus) {
//                logger.info("系统设置为单点登录");
            //如果redis配置为开启状态，那么将token存储到redis中
            if (redisOpenStatus) {
//                    logger.info("redis开启，将用户登录token存储在redis中");
                String redisKey = RedisKeys.getUserAppTokenKey(currentUserId + "");
                String redisToken = jwt.substring(jwt.length() - 30);
                redisUtils.set(redisKey, redisToken, tokenValidityInSeconds);
            }
        }
        SysUserBean sysUserBean = sysUserService.selectByPrimaryKey(currentUserId);
        AppLoginReturnDTO appLoginReturnDTO = new AppLoginReturnDTO();
        BeanUtils.copyProperties(sysUserBean, appLoginReturnDTO);
        appLoginReturnDTO.setId_token(jwt);
        return appLoginReturnDTO;
    }

    @GetMapping(value = "/authenticate/authority")
    public Result appAuthority() {
        SysUserBean sysUserBean = SecurityUtils.getCurrentUser();
        logger.info("Start 》UserJWTController.appAuthority() user:{}", JSONObject.toJSON(sysUserBean));
        if (sysUserBean == null) {
            return new Result(404, "请先登录！");
        }

        List<Long> roleList = Constants.getIdListByStr(sysUserBean.getRoleIds());
        String menuCodeStr = sysUserService.getRoleIdsByMenuCode(roleList);
        List<String> menuCodeList = new ArrayList<>();
        if (!StringUtils.isEmpty(menuCodeStr)) {
            String[] codeArr = menuCodeStr.split(",");
            menuCodeList = Arrays.asList(codeArr);
        }
        logger.info("End 》UserJWTController.appAuthority() 返回：{}", JSONObject.toJSON(menuCodeList));
        return new Result(menuCodeList);
    }


    /**
     * 获取验证码
     *
     * @param response
     * @throws ServletException
     * @throws IOException
     */
    @GetMapping(value = "/captcha.jpg", params = "redisKey")
    public void captcha(@RequestParam String redisKey, HttpServletResponse response) throws ServletException, IOException {
        DefaultKaptcha defaultKaptcha = kaptchaConfig.getDefaultKaptcha();
        response.setHeader("Cache-Control", "no-store, no-cache");
        //response.addHeader("Cache-Control", "post-check=0, pre-check=0");
        //response.setContentType("image/*");
        response.setContentType("image/png");

        String kaptchat = redisUtils.get(redisKey);

        //生成图片验证码
        BufferedImage image = defaultKaptcha.createImage(kaptchat);
        ServletOutputStream out = response.getOutputStream();
        ImageIO.write(image, "png", out);
    }


    /**
     * 获取验证码KEY
     *
     * @param
     * @throws ServletException
     * @throws IOException
     */
    @GetMapping(value = "/redisKey")
    public Result redisKey() throws IOException {
        DefaultKaptcha defaultKaptcha = kaptchaConfig.getDefaultKaptcha();
        //生成文字验证码
        String text = defaultKaptcha.createText();
        String redisKey = RedisKeys.getRedisKey();
        redisUtils.set(redisKey, text, 3600);
        System.out.println(">>>>>>>>>>>>>>>>>>生成的验证码是<<<<<<<<<<<<<<==" + redisUtils.get(redisKey));
        Map<String, String> map = new HashMap<>();
        map.put("redisKey", redisKey);
        return new Result(map);
    }


}
