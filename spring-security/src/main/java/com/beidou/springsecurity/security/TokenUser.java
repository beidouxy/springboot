package com.beidou.springsecurity.security;

import com.beidou.springsecurity.bean.SysUserBean;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;

import java.util.Collection;

public class TokenUser extends User {

	private static final long serialVersionUID = 7958134989538914636L;

	public TokenUser(String username, String password, Collection<? extends GrantedAuthority> authorities) {
		super(username, password, authorities);
	}
	
	private Long id = null;

	private SysUserBean sysUserBean = null;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public SysUserBean getSysUserBean() {
		return sysUserBean;
	}

	public void setSysUserBean(SysUserBean sysUserBean) {
		this.sysUserBean = sysUserBean;
	}
}
