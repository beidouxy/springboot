package com.beidou.rabbitmq.service;

import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @Author: Evan.Wei
 * @Date: 2018/12/19 17:00
 */
@Component
public class TopicSenderService {

    @Autowired
    private AmqpTemplate rabbitTemplate;

    public void send() {
        String msg1 = "I am topic.mesaage msg======";
        System.out.println("sender1 : " + msg1);
        this.rabbitTemplate.convertAndSend("exchange", "topic.message", msg1);

        String msg2 = "I am topic.mesaages msg########";
        System.out.println("sender2 : " + msg2);

        // 队列topic.messages与exchange绑定，binding_key为topic.#,模糊匹配
        this.rabbitTemplate.convertAndSend("exchange", "topic.messages", msg2);
    }

}
