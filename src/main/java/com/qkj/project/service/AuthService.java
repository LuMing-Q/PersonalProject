package com.qkj.project.service;

import com.qkj.project.common.Online;
import com.qkj.project.common.enumerations.VerifyResult;

import java.util.Map;

/**
 * @author KeJiang Qi
 * @date 2024/8/22 - 10:55
 * @description 登录相关授权
 */
public interface AuthService {

    /**
     * 生成随机编码
     * @return 随机编码
     */
    default String randomCode() {
        return null;
    }

    /**
     * 登录系统
     * @param params 登录参数
     * @return 登录凭证
     */
    Online login(Map<String, String> params);

    /**
     * 二次登录
     * @param params 二次登录参数
     * @return 登录凭证
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
     * @param token 登录凭证
     * @return 校验结果
     */
    VerifyResult verify(String token);
}
