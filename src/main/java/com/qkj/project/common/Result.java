package com.qkj.project.common;

import com.qkj.project.common.enumerations.StatusCode;
import com.qkj.project.utils.BaseUtil;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

/**
 * @param <T>
 * @author KeJiang Qi
 * @date 2024/8/20 - 14:57
 * @description 统一信息返回类
 */
@Setter
@Getter
public class Result<T> implements Serializable {
    private String logid;
    private int code;
    private String msg;
    private String info;
    private T data;

    public static <T> Result<T> ok(T data) {
        return of(StatusCode.CODE_200.getCode(), "操作成功", data);
    }

    public static <T> Result<T> failed(int code, String msg) {
        return of(code, msg, null);
    }

    public static <T> Result<T> failed(int code, String msg, String errInfo) {
        return of(code, msg, errInfo);
    }

    public static <T> Result<T> of(int code, String msg, T data) {
        return of(code, msg, null, data);
    }

    public static <T> Result<T> of(int code, String msg, String errInfo) {
        return of(code, msg, errInfo, null);
    }

    public static <T> Result<T> of(int code, String msg, String info, T data) {
        Result<T> result = new Result<>();
        result.setCode(code);
        result.setMsg(msg);
        result.setInfo(info);
        result.setData(data);
        RequestHolder.Value value = RequestHolder.get();
        result.setLogid(value != null ? value.getLogId() : BaseUtil.uuid());
        return result;
    }

}
