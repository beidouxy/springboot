package com.beidou.springsecurity.service.impl;

import com.beidou.springsecurity.bean.SysUserBean;
import com.beidou.springsecurity.mapper.SysUserMapper;
import com.beidou.springsecurity.service.SysUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @Author: Evan.Wei
 * @Date: 2018/11/22 14:10
 */
@Service
public class SysUserServiceImpl implements SysUserService {

    @Autowired
    private SysUserMapper sysUserMapper;

    @Override
    public SysUserBean selectByPrimaryKey(Long id) {
        return sysUserMapper.selectByPrimaryKey(id);
    }

    @Override
    public String getRoleIdsByMenuCode(List<Long> roleList) {
        return sysUserMapper.getRoleIdsByMenuCode(roleList);
    }
}
