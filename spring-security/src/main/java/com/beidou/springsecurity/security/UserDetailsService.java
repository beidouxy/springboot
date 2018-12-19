package com.beidou.springsecurity.security;

import com.beidou.springsecurity.bean.SysUserBean;
import com.beidou.springsecurity.mapper.SysUserMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Optional;

/**
 * Authenticate a user from the database.
 */
@Component("userDetailsService")
public class UserDetailsService implements org.springframework.security.core.userdetails.UserDetailsService {

    private final Logger log = LoggerFactory.getLogger(UserDetailsService.class);

    @Autowired
    private SysUserMapper sysUserMapper;

    @Override
    public UserDetails loadUserByUsername(final String username) {
        if(log.isDebugEnabled()){
            log.debug("Authenticating:[用户名:{}]", username);
        }
        //XXX:状态码 0,1 放到静态类中去
        Optional<SysUserBean> userFromDatabase = Optional.ofNullable(sysUserMapper.findOneByUserNameAndStatus(username));
        return userFromDatabase.map(user -> {
            //XXX:状态码 0,1 放到静态类中去
            if (user.getStatus().equals(0)) {
                throw new UserNotActivatedException("用户" + username + "已被禁用，请联系管理员！");
            }
            BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
            // 加密
            String encodedPassword = passwordEncoder.encode(user.getPassword().trim());
            TokenUser tokenUser = new TokenUser(username, encodedPassword,  new ArrayList<>());
            tokenUser.setId(user.getId());
            tokenUser.setSysUserBean(user);
            return tokenUser;
        }).orElseThrow(() -> new UsernameNotFoundException("用户 " + username
                                + " 不存在"));
    }
}
