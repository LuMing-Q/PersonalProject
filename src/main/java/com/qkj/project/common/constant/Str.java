package com.qkj.project.common.constant;

/**
 * @author KeJiang Qi
 * @date 2024/8/23 - 14:11
 * @description 缓存键值常量类
 */
public class Str {
// ==================================== redis 相关常量 ====================================
    /**
     * redis的存储数据键值的前缀
     */
    public static String REDIS_KEY_PREFIX = "qkj_project:";

    /**
     * 用户登录TOKEN信息缓存
     */
    public static String ONLINE_TOKEN = REDIS_KEY_PREFIX + "online:%s:token";

// ==================================== 数据加密 相关常量 ====================================
    /**
     * 登录使用密码加解密的秘钥
     */
    public static final String PASS_SECRET = "21CMWDOSEilckmyl";

// ==================================== rabbit 相关常量 ====================================
    /**
     * 消息通知 Routing Key 匹配前缀
     */
    public static final String INFORM_MADE = "inform.%s";

    /**
     * 短信通知
     */
    public static final String SMS = "sms";

    /**
     * 站内消息通知
     */
    public static final String STATION = "station";

    /**
     * 操作日志路由键
     */
    public static final String OPTION_MADE = "option.log";
}
