package com.beidou.springsecurity.security.jwt;

import com.alibaba.fastjson.JSONObject;
import com.beidou.springsecurity.bean.SysUserBean;
import com.beidou.springsecurity.config.ApplicationProperties;
import com.beidou.springsecurity.security.TokenUser;
import com.beidou.springsecurity.utils.redis.RedisKeys;
import com.beidou.springsecurity.utils.redis.RedisUtils;
import io.jsonwebtoken.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.Date;
import java.util.stream.Collectors;

@Component
public class TokenProvider {

    private final Logger log = LoggerFactory.getLogger(TokenProvider.class);

    private static final String AUTHORITIES_KEY = "auth";
    private static final String TOKEN_USER_KEY = "token_user";
    private static final String TOKEN_USER_ID_KEY = "token_user_id";

    private String secretKey;

    private long tokenValidityInSeconds;

    private long tokenValidityInSecondsForRememberMe;

    @Autowired
    private ApplicationProperties properties;

    @Autowired
    private RedisUtils redisUtils;

    @Value(value = "${spring.redis.open}")
    private boolean redisOpenStatus;
    @Value(value = "${Login.singleLogin}")
    private boolean singleLoginStatus;

    @PostConstruct
    public void init() {
        this.secretKey =
        		properties.getSecurity().getAuthentication().getJwt().getSecret();
        this.tokenValidityInSeconds =
            1000 * properties.getSecurity().getAuthentication().getJwt().getTokenValidityInSeconds();
        this.tokenValidityInSecondsForRememberMe =
            1000 * properties.getSecurity().getAuthentication().getJwt().getTokenValidityInSecondsForRememberMe();
    }

    public String createToken(Authentication authentication, Boolean rememberMe) {
        String authorities = authentication.getAuthorities().stream()
            .map(authority -> authority.getAuthority())
            .collect(Collectors.joining(","));
        TokenUser tokenUser = (TokenUser)authentication.getPrincipal();
        String json = JSONObject.toJSONString(tokenUser.getSysUserBean());
        return Jwts.builder()
            .setSubject(authentication.getName())
            .claim(AUTHORITIES_KEY, authorities)
            .claim(TOKEN_USER_KEY, json)
            .claim(TOKEN_USER_ID_KEY, tokenUser.getId())
            .signWith(SignatureAlgorithm.HS512, secretKey)
            .compact();
    }

    public Authentication getAuthentication(String token) {
        Claims claims = Jwts.parser()
            .setSigningKey(secretKey)
            .parseClaimsJws(token)
            .getBody();

        String json = claims.get(TOKEN_USER_KEY).toString();

        TokenUser principal = new TokenUser(claims.getSubject(), "", new ArrayList<GrantedAuthority>());
        principal.setSysUserBean(JSONObject.parseObject(json, SysUserBean.class));
        principal.setId(Long.valueOf(String.valueOf(claims.get(TOKEN_USER_ID_KEY))));

        return new UsernamePasswordAuthenticationToken(principal, "", new ArrayList<GrantedAuthority>());
    }

    public boolean validateToken(String authToken) {
        try {
            Jws<Claims> parseClaimsJws = Jwts.parser().setSigningKey(secretKey).parseClaimsJws(authToken);
            Date expirationDate = parseClaimsJws.getBody().getExpiration();
            if(expirationDate.before(new Date())){
            	return false;
            }
            return true;
        } catch (SignatureException e) {
            log.info("Invalid JWT signature: " + e.getMessage());
            return false;
        }
    }

    public boolean validateRedisToken(String authToken) {
        try {
            if (singleLoginStatus) {
                Claims claims = Jwts.parser()
                        .setSigningKey(secretKey)
                        .parseClaimsJws(authToken)
                        .getBody();
                //获取用户登录的redisKey
                String redisKey = RedisKeys.getUserAppTokenKey(String.valueOf(claims.get(TOKEN_USER_ID_KEY)));
                log.info("Start 1》TokenProvider.validateRedisToken(redisKey = {})", JSONObject.toJSON(redisKey));
                //根据accessToken，查询用户信息
                if (redisOpenStatus) {
                    String redisToken = redisUtils.get(redisKey);
                    String tempToken = authToken.substring(authToken.length() - 30);
                    log.info("Start 2》TokenProvider.validateRedisToken(redisToken = {}, tempToken = {})", JSONObject.toJSON(redisToken),JSONObject.toJSON(tempToken));
                    if (redisToken != null && redisToken.equals(tempToken)) {
                        //刷新redis时间，续命
                        redisUtils.set(redisKey, tempToken, tokenValidityInSeconds/1000);
                        log.info("Start 3》TokenProvider.validateRedisToken(tempToken = {})", JSONObject.toJSON(tempToken));
                        return true;
                    }
                }
            }
            log.info("End 》TokenProvider.validateRedisToken()  失败");
            return false;
        } catch (SignatureException e) {
            log.info("validateRedisToken-Invalid JWT signature: " + e.getMessage());
            return false;
        }
    }
}
