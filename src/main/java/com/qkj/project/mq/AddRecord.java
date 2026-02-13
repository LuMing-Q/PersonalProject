package com.qkj.project.mq;

import com.qkj.project.dao.LocalMessageDao;
import com.qkj.project.entity.LocalMessage;
import com.qkj.project.utils.BaseUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.time.LocalDateTime;

/**
 * @author KeJiang Qi
 * @date 2026/2/13 - 14:46
 * @description 添加消息记录
 */
@Slf4j
@Service
public class AddRecord {

    @Resource
    private LocalMessageDao localMessageDao;

    /**
     * 发送可靠消息
     * @param exchange 交换机名称
     * @param routingKey 路由键
     * @param message 消息
     */
    @Transactional(rollbackFor = Exception.class)
    public LocalMessage sendReliable(String exchange, String message, String routingKey) {
        log.debug("Send message {} to exchange {} routing key {}", message, exchange, routingKey);
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
        return local;
    }
}
