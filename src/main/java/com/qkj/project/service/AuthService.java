package com.qkj.project.service;

import com.qkj.project.common.Online;

import java.util.Map;

/**
 * @author KeJiang Qi
 * @date 2024/8/22 - 10:55
 * @description 登录相关授权
 */
public interface AuthService {

    /**
     * 生成随机编码
     * @return
     */
    default String randomCode() {
        return null;
    }

    /**
     * 登录系统
     * @param params
     * @return
     */
    Online login(Map<String, String> params);

    /**
     * 二次登录
     * @param params
     * @return
     */
    default Online sLogin(Map<String, String> params) {
        return null;
    }

    /**
     * 登出系统
     */
    void logout();

    /**
     * token 校验、解析
     * @param token
     * @return 1-Token 格式异常, 2-Token 解析失败, 3-Token 已过期, 4-用户不存在
     */
    int verify(String token);
}
