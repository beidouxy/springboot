package com.beidou.rabbitmq.consumer;

import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * @Author: Evan.Wei
 * @Date: 2018/12/19 14:45
 * @desc
 * 队列发送对象
 */
@Component
@RabbitListener(queues = "userQueue")
public class UserReceiver {

    @RabbitHandler
    public void process(Map<String, String> map) {
        System.out.println("Receiver  : name(" + map.get("name") + "),性别(" + map.get("sex") + ").");
    }

}
