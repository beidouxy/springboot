package com.beidou.servicefeign.service;

import com.beidou.servicefeign.service.impl.SchedualServiceHiHystric;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * @Author: Evan.Wei
 * @Date: 2018/11/14 14:07
 * 定义一个feign接口，通过@ FeignClient（“服务名”），来指定调用哪个服务
 *
 * SchedualServiceHi接口的注解中加上fallback的指定类
 */
@FeignClient(value = "service-hi",fallback = SchedualServiceHiHystric.class)
public interface SchedualServiceHi {
    @RequestMapping(value = "/hi",method = RequestMethod.GET)
    String sayHiFromClientOne(@RequestParam(value = "name") String name);
}