package com.qkj.project.mq;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

/**
 * @author KeJiang Qi
 * @date 2025/7/29 - 9:37
 * @description 报表生成消息消费者
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class ReportMessageConsumer {

    @RabbitListener(queues = "report.generate.queue")
    public void handleReportGeneration(String message) {
        log.info("处理逻辑 {}", message);
    }
}
