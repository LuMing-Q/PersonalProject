package com.qkj.project.entity;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * @author KeJiang Qi
 * @date 2026/2/12 - 09:59
 * @description RabbitMQ 本地消息实体类
 */
@Data
public class LocalMessage {
    private String id;
    private String exchangeName;
    private String routingKey;
    private String message;
    /**
     * 状态 0待发送 1成功 2失败 3死亡
     */
    private Integer status;
    private Integer retryCount;
    private LocalDateTime nextRetryTime;
    private Boolean consumed;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}

