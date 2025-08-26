package com.qkj.project.mq;

import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

/**
 * @author KeJiang Qi
 * @date 2025/7/29 - 9:25
 * @description MQ消息生产者
 */
@Component
@RequiredArgsConstructor
public class RabbitMessageProducer {
    private final RabbitTemplate rabbitTemplate;

    public void send(RabbitMessage message) {
        rabbitTemplate.convertAndSend("report.exchange", "report.generate", message);
    }
}
