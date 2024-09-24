package com.qkj.project.cache;

import com.qkj.project.common.Online;

/**
 * @author KeJiang Qi
 * @date 2024/8/23 - 14:09
 * @description
 */
public interface OnlineCache {
    /**
     * 通过 token 查询当前登录用户相关信息
     * @param token
     * @return
     */
    Online getOnline(String token);

    /**
     * 存储 token(key) 和当前登路用户的相关信息（value）
     * @param token
     * @param online
     */
    void cacheOnline(String token, Online online);

    /**
     * 通过 token 删除已登陆用户信息
     * @param token
     */
    void delOnline(String token);
}
