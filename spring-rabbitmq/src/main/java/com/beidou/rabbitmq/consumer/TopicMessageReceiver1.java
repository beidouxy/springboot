package com.beidou.rabbitmq.consumer;

import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

/**
 * @Author: Evan.Wei
 * @Date: 2018/12/19 17:03
 * @Desc
 *  topic exchange类型，消费者接收消息，topic.message
 */
@Component
@RabbitListener(queues = "topic.message")
public class TopicMessageReceiver1 {

    @RabbitHandler
    public void process(String msg) {
        System.out.println("topicMessageReceiver1  : " +msg);
    }
}
