package com.beidou.springsecurity.mapper;

import com.beidou.springsecurity.bean.SysUserBean;

import java.util.List;

public interface SysUserMapper {

    /**
     * 根据用户id，获取用户信息
     * @param id 用户id
     * @return SysUser用户信息
     */
    SysUserBean selectByPrimaryKey(Long id);

    /**
     * 获取用户权限code
     * @param roleList 用户权限id
     * @return 权限code,逗号拼接的字符串
     */
    String getRoleIdsByMenuCode(List<Long> roleList);

    /**
     * 根据username查询用户信息
     * @param username 用户名
     * @return 用户信息
     */
    SysUserBean findOneByUserNameAndStatus(String username);

}