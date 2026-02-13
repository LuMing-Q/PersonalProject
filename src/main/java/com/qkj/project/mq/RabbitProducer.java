package com.qkj.project.mq;

import com.alibaba.fastjson.JSON;
import com.qkj.project.common.constant.Str;
import com.qkj.project.dao.LocalMessageDao;
import com.qkj.project.entity.LocalMessage;
import com.qkj.project.utils.BaseUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.MessageDeliveryMode;
import org.springframework.amqp.rabbit.connection.CorrelationData;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.List;

/**
 * @author KeJiang Qi
 * @date 2025/7/29 - 9:25
 * @description MQ 生产者
 */
@Slf4j
@Component
@EnableScheduling
public class RabbitProducer {
    private final RabbitTemplate rabbitTemplate;

    @Value("${mq.exchange.option-log}")
    private String optionLogExchange;

    @Value("${mq.exchange.inform}")
    private String informExchange;

    @Resource
    private LocalMessageDao localMessageDao;

    public RabbitProducer(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
        // 设置 Mandatory 为 true，确保消息被路由到队列失败时触发 Return 回调
        rabbitTemplate.setMandatory(true);
        initCallback();
    }

    /**
     * 初始化 Confirm + Return 回调
     */
    private void initCallback() {
        // Confirm 回调（是否到达 Exchange）
        rabbitTemplate.setConfirmCallback((correlationData, ack, cause) -> {
            if (correlationData == null) { return; }
            String msgId = correlationData.getId();
            if (ack) {
                log.debug("消息成功到达 Exchange: {}", msgId);
                localMessageDao.updateStatusSuccess(msgId);
            } else {
                log.error("消息未到达 Exchange: {}, 原因: {}", msgId, cause);
                localMessageDao.updateStatusFail(msgId);
            }
        });
        // Return 回调（是否成功路由到队列）
        rabbitTemplate.setReturnsCallback(returned -> {
            String msgId = returned.getMessage()
                    .getMessageProperties()
                    .getCorrelationId();
            log.error("消息未路由到队列: msgId={}, exchange={}, routingKey={}",
                    msgId,
                    returned.getExchange(),
                    returned.getRoutingKey());
            if (msgId != null) {
                localMessageDao.updateStatusFail(msgId);
            }
        });
    }

    /**
     * 构建消息并调用 RabbitTemplate 发送消息到指定交换机
     * @param exchange 交换机名称
     * @param message 推送消息内容
     */
    public void send(String exchange, Object message) {
        if (message instanceof String) {
            send(exchange, (String) message);
        }
        send(exchange, JSON.toJSONString(message));
    }

    /**
     * 构建消息并调用 RabbitTemplate 发送字符串消息到指定交换机
     * @param exchange 交换机名称
     * @param message 消息内容
     */
    public void send(String exchange, String message) {
        sendReliable(exchange, message, "");
    }

    /**
     * 发送消息到选项日志交换机
     * @param message 消息内容
     */
    public void sendOptionLog(String message) {
        sendReliable(optionLogExchange, message, Str.OPTION_MADE);
    }

    /**
     * 发送消息到通知交换机
     * @param message 消息内容
     */
    public void sendInform(RabbitMessage message) {
        sendReliable(informExchange, JSON.toJSONString(message), String.format(Str.INFORM_MADE, message.getInformMade()));
    }

    /**
     * 发送可靠消息
     * @param exchange 交换机名称
     * @param routingKey 路由键
     * @param message 消息
     */
    public void sendReliable(String exchange, String message, String routingKey) {
        log.debug("Send message {} to exchange {}, routing key {}", message, exchange, routingKey);
        String msgId = BaseUtil.uuid();
        LocalMessage local = new LocalMessage();
        local.setId(msgId);
        local.setExchangeName(exchange);
        local.setRoutingKey(routingKey);
        local.setMessage(message);
        local.setStatus(0);
        local.setRetryCount(0);
        local.setNextRetryTime(LocalDateTime.now());
        local.setCreateTime(LocalDateTime.now());
        local.setUpdateTime(LocalDateTime.now());
        localMessageDao.insert(local);
        // 事务提交后再发MQ（避免脏消息）
        if (TransactionSynchronizationManager.isActualTransactionActive()) {
            TransactionSynchronizationManager.registerSynchronization(
                    new TransactionSynchronization() {
                        @Override
                        public void afterCommit() {
                            sendToRabbit(local);
                        }
                    }
            );
        } else {
            // 如果本身没有事务，直接发送
            sendToRabbit(local);
        }
    }

    /**
     * 发送消息到MQ
     * @param local 消息对象
     */
    @Async("rabbitExecutor")
    public void sendToRabbit(LocalMessage local) {
        CorrelationData correlationData = new CorrelationData(local.getId());
        rabbitTemplate.convertAndSend(
                local.getExchangeName(),
                local.getRoutingKey(),
                local.getMessage(),
                msg -> {
                    msg.getMessageProperties().setCorrelationId(local.getId());
                    msg.getMessageProperties().setContentType("application/json");
                    msg.getMessageProperties().setDeliveryMode(MessageDeliveryMode.PERSISTENT);
                    return msg;
                },
                correlationData
        );
    }

    @Scheduled(fixedDelay = 1000 * 60 * 5)
    public void sendLocalMessage() {
        List<LocalMessage> list = localMessageDao.selectNeedSentMessage();
        for (LocalMessage msg : list) {
            if (msg.getStatus() == 0) {
                sendToRabbit(msg);
                continue;
            }
            if (msg.getRetryCount() >= 5) {
                localMessageDao.updateStatus(msg.getId(), 3);
                log.error("消息进入死亡状态: {}", msg.getId());
                continue;
            }
            int retry = msg.getRetryCount() + 1;
            msg.setRetryCount(retry);
            // 指数退避
            msg.setNextRetryTime(LocalDateTime.now().plusSeconds((long) Math.pow(2, retry)));
            localMessageDao.updateMessage(msg);
            sendToRabbit(msg);
        }
    }
}