package com.qkj.project.common.enumerations;

import java.util.stream.Stream;

/**
 * @author KeJiang Qi
 * @date 2024/8/20 - 14:57
 * @description 状态码枚举类
 */
public enum StatusCode {
    /**
     * 成功
     */
    CODE_200(200),
    CODE_201(201),
    CODE_202(202),
    CODE_203(203),
    CODE_204(204),
    CODE_206(206),
    CODE_300(300),
    CODE_400(400),
    CODE_401(401),
    CODE_402(402),
    CODE_403(403),
    CODE_404(404),
    CODE_405(405),
    CODE_406(406),
    CODE_407(407),
    CODE_408(408),
    CODE_409(409),
    CODE_410(410),
    CODE_411(411),
    CODE_412(412),
    CODE_413(413),
    CODE_415(415),
    CODE_500(500),
    CODE_501(501),
    CODE_502(502),
    CODE_503(503),
    CODE_504(504);

    private final int code;
    StatusCode(int code) {
        this.code = code;
    }
    public int getCode() {
        return this.code;
    }
    public static StatusCode valueof(int code) {
        return Stream.of(values()).filter(v -> v.getCode() == code).findFirst().orElse(null);
    }
    public boolean eq(int code) {
        return this.getCode() == code;
    }
}
