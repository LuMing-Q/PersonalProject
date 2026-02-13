package com.qkj.project.mq;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.experimental.Accessors;

import java.time.LocalDateTime;

/**
 * @author KeJiang Qi
 * @date 2025/8/22 - 15:23
 * @description RabbitMQ 消息传递实体类
 */
@Data
@Accessors(chain = true)
public class RabbitMessage {
    private String id;
    @JsonProperty("user_id")
    private String userId;
    private boolean read;
    private String title;
    private String context;
    @JsonInclude(JsonInclude.Include.NON_DEFAULT)
    private int level;
    @JsonProperty("create_time")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createTime;
    /**
     * 匹配队列 sms 短信通知 sta 站内消息
     */
    @JsonIgnore
    private String informMade;
}
