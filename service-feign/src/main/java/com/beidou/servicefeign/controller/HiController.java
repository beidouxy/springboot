package com.beidou.servicefeign.controller;

import com.beidou.servicefeign.service.SchedualServiceHi;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * @Author: Evan.Wei
 * @Date: 2018/11/14 14:09
 * 在Web层的controller层，对外暴露一个"/hi"的API接口，
 * 通过定义的Feign客户端SchedualServiceHi 来消费服务。
 */
@RestController
public class HiController {

    @Autowired
    SchedualServiceHi schedualServiceHi;

    @GetMapping(value = "/hi")
    public String sayHi(@RequestParam String name) {
        return schedualServiceHi.sayHiFromClientOne( name );
    }

}
