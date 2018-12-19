package com.beidou.rabbitmq.consumer;

import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

/**
 * @Author: Evan.Wei
 * @Date: 2018/12/19 17:03
 * @Desc
 *  topic exchange类型，消费者接收消息，topic.#
 */
@Component
@RabbitListener(queues = "topic.messages")
public class TopicMessageReceiver2 {

    @RabbitHandler
    public void process(String msg) {
        System.out.println("topicMessagesReceiver2  : " +msg);
    }
}
