package com.beidou.rabbitmq.service;

import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * @Author: Evan.Wei
 * @Date: 2018/12/19 14:35
 */
@Component
public class UserSenderService {

    @Autowired
    private AmqpTemplate rabbitTemplate;

    public void send() {
        Map<String,String> map = new HashMap<>();
        map.put("name", "beidou");
        map.put("sex", "男");
        this.rabbitTemplate.convertAndSend("userQueue", map);
    }

}
