package com.qkj.project.common.constant;

/**
 * @author KeJiang Qi
 * @date 2024/8/23 - 14:11
 * @description
 */
public class RedisKey {

    // redis的存储数据键值的前缀
    public static String REDIS_KEY_PREFIX = "qkj_project:";

    // 用户登录TOKEN信息缓存
    public static String ONLINE_TOKEN = REDIS_KEY_PREFIX + "online:%s:token";
}
