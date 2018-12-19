package com.beidou.servicefeign.service.impl;

import com.beidou.servicefeign.service.SchedualServiceHi;
import org.springframework.stereotype.Component;

/**
 * @Author: Evan.Wei
 * @Date: 2018/11/14 14:32
 * SchedualServiceHi接口的注解中加上fallback的指定类
 */
@Component
public class SchedualServiceHiHystric implements SchedualServiceHi {

    @Override
    public String sayHiFromClientOne(String name) {
        return "Sorry "+name;
    }
}
