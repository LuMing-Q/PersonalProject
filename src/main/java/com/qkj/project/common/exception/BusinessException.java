package com.qkj.project.common.exception;

import com.qkj.project.common.enumerations.StatusCode;

/**
 * @author KeJiang Qi
 * @date 2024/8/20 - 14:57
 * @description 自定义异常类
 */
public class BusinessException extends RuntimeException {
    private int code;
    private String msg;

    public BusinessException(int code, String message) {
        super(message);
        this.code = code;
    }

    public BusinessException(int code, String message, String msg) {
        super(message);
        this.code = code;
        this.msg = msg;
    }

    public static BusinessException of(StatusCode code, String message) {
        return of(code.getCode(), message, "");
    }

    public static BusinessException of(StatusCode code, String message, String msg) {
        return of(code.getCode(), message, msg);
    }

    public static BusinessException of(BusinessException exception, String message) {
        return new BusinessException(exception.getCode(), message);
    }

    public static BusinessException of(int code, String message) {
        return of(code, message, "");
    }

    public static BusinessException of(int code, String message, String msg) {
        return new BusinessException(code, message, msg);
    }

    public int getCode() {
        return code;
    }

    public String getMsg() {
        return msg;
    }
}
