package com.qkj.project.common;

import com.alibaba.fastjson.annotation.JSONField;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * @author KeJiang Qi
 * @date 2024/8/20 - 14:57
 * @description 当前线程相关信息
 */
public class RequestHolder {

    private final static ThreadLocal<Value> requestHolder = new ThreadLocal<>();

    public static void add(Value value) {
        requestHolder.set(value);
    }

    public static Value get() {
        return requestHolder.get();
    }

    public static Value remove() {
        Value v = requestHolder.get();
        requestHolder.remove();
        return v;
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Value extends Online {

        @JSONField(serialize = false)
        private String logId;
        public Value(String logId, Online online) {
            super(online.getToken(), online.getUser());
            this.logId = logId;
        }

        public void setOnline(Online online) {
            super.setToken(online.getToken());
            super.setUser(online.getUser());
        }
    }
}
