package com.qkj.project.common.enumerations;

import lombok.Getter;

/**
 * @author KeJiang Qi
 * @date 2026/2/5 - 14:12
 * @description Token验证结果枚举类
 */
@Getter
public enum VerifyResult {
    // 验证通过
    SUCCESS(0, null),
    INVALID_FORMAT(1, "Token 格式异常"),
    PARSE_FAILED(2, "Token 解析失败"),
    EXPIRED(3, "Token 已过期"),
    USER_NOT_FOUND(4, "用户不存在");

    private final int code;
    private final String message;

    VerifyResult(int code, String message) {
        this.code = code;
        this.message = message;
    }

    public boolean isSuccess() { return this == SUCCESS; }
}
