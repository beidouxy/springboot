package com.beidou.rabbitmq.controller;

import com.beidou.rabbitmq.service.HelloSenderService;
import com.beidou.rabbitmq.service.UserSenderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @Author: Evan.Wei
 * @Date: 2018/12/19 14:37
 */
@RestController
@RequestMapping("rabbit")
public class HelloSenderController {

    @Autowired
    private HelloSenderService helloSenderService;

    @GetMapping("hello")
    public String helloSender() {
        helloSenderService.send();
        return "一对一发送成功";
    }

    /**
     * 单生产者-多消费者
     */
    @GetMapping("/oneToMany")
    public String oneToMany() {
        for (int i = 0; i < 10; i++) {
            helloSenderService.send("hellomsg:" + i);

            helloSenderService.send();
        }
        return "单生产者-多消费者发送成功";
    }

    @Autowired
    private UserSenderService userSenderService;

    @GetMapping("user")
    public String userSender() {
        userSenderService.send();
        return "对象信息发送成功";
    }

}
