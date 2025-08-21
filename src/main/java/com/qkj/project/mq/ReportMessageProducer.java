package com.qkj.project.mq;

import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

/**
 * @author KeJiang Qi
 * @date 2025/7/29 - 9:25
 * @description 消息生产者
 */
@Component
@RequiredArgsConstructor
public class ReportMessageProducer {
    private final RabbitTemplate rabbitTemplate;

    public void send(String message) {
        rabbitTemplate.convertAndSend("report.exchange", "report.generate", message);
    }
}
