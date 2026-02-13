package com.qkj.project.mq;

import com.alibaba.fastjson.JSON;
import com.qkj.project.dao.LocalMessageDao;
import com.qkj.project.dao.OptionLogDao;
import com.qkj.project.entity.OptionLog;
import com.rabbitmq.client.Channel;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.io.IOException;

/**
 * @author KeJiang Qi
 * @date 2025/7/29 - 9:37
 * @description MQ 消费者
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class RabbitConsumer {

    @Resource
    private OptionLogDao optionLogDao;

    @Resource
    private LocalMessageDao localMessageDao;

    @RabbitListener(queues = "${mq.queue.option-log}")
    public void consumeOptionLog(String body, Channel channel, Message message) throws IOException {
        long tag = message.getMessageProperties().getDeliveryTag();
        String msgId = message.getMessageProperties().getCorrelationId();
        try {
            if (localMessageDao.isConsumed(msgId)) {
                channel.basicAck(tag, false);
                return;
            }
            OptionLog optionLog = JSON.parseObject(body, OptionLog.class);
            optionLog.setYear(optionLog.getCreateTime().getYear());
            optionLogDao.upsert(optionLog);
            int row = localMessageDao.markConsumed(msgId);
            if (row == 0) {
                return;
            }
            channel.basicAck(tag, false);
        } catch (Exception e) {
            log.error("消费失败: {}", msgId);
            // 不重新入队 → 进入死信队列
            channel.basicNack(tag, false, false);
        }
    }


    @RabbitListener(queues = "${mq.queue.inform-station}")
    public void consumeInStationMessage(String body, Channel channel, Message message) throws IOException {
        RabbitMessage msg = JSON.parseObject(body, RabbitMessage.class);
        log.info("Start consume station message: {}", msg);
        long tag = message.getMessageProperties().getDeliveryTag();
        String msgId = message.getMessageProperties().getCorrelationId();
        try {
            if (localMessageDao.isConsumed(msgId)) {
                // 已消费 → 直接确认
                channel.basicAck(tag, false);
                return;
            }
            // todo 处理站内消息
            int row = localMessageDao.markConsumed(msgId);
            if (row == 0) {
                return;
            }
            channel.basicAck(tag, false);
        } catch (Exception e) {
            log.error("站内消息消费失败: {}", msgId);
            // 不重新入队 → 进入死信队列
            channel.basicNack(tag, false, false);
        }
    }

    @RabbitListener(queues = "${mq.queue.inform-sms}")
    public void consumeSmsMessage(String body, Channel channel, Message message) throws IOException {
        RabbitMessage msg = JSON.parseObject(body, RabbitMessage.class);
        log.info("Start consume sms message: {}", msg);
        long tag = message.getMessageProperties().getDeliveryTag();
        String msgId = message.getMessageProperties().getCorrelationId();
        try {
            if (localMessageDao.isConsumed(msgId)) {
                // 已消费 → 直接确认
                channel.basicAck(tag, false);
                return;
            }
            // todo 处理站内消息
            int row = localMessageDao.markConsumed(msgId);
            if (row == 0) {
                return;
            }
            channel.basicAck(tag, false);
        } catch (Exception e) {
            log.error("短信消息消费失败: {}", msgId);
            // 不重新入队 → 进入死信队列
            channel.basicNack(tag, false, false);
        }
    }
}