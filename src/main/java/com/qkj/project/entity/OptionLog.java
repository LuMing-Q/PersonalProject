package com.qkj.project.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * @author KeJiang Qi
 * @date 2024/8/20 - 15:26
 * @description
 */
@Data
public class OptionLog {
    private String id;
    private String userId;
    private String userName;
    private String operate;
    private int status;
    /**
     * token SHA256加密值 用于识别同一次登录操作
     */
    private String relation;
    private String path;
    private String param;
    private String result;
    private String wrong;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createTime;
}
