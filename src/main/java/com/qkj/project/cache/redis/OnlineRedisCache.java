package com.qkj.project.cache.redis;

import com.alibaba.fastjson.JSON;
import com.qkj.project.cache.OnlineCache;
import com.qkj.project.common.Online;
import com.qkj.project.common.constant.RedisKey;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.concurrent.TimeUnit;

/**
 * @author KeJiang Qi
 * @date 2024/8/23 - 14:10
 * @description
 */
@Component
public class OnlineRedisCache implements OnlineCache {

    @Resource
    private RedisTemplate<String, Object> redisTemplate;

    @Value("${auth.login-expire-time}")
    private long loginExpireTime;

    @Override
    public Online getOnline(String token) {
        String key = String.format(RedisKey.ONLINE_TOKEN, token);
        return JSON.parseObject((String) redisTemplate.boundValueOps(key).get(), Online.class);
    }

    @Override
    public void cacheOnline(String token, Online online) {
        String key = String.format(RedisKey.ONLINE_TOKEN, token);
        Boolean hasKey = redisTemplate.hasKey(key);
        if (hasKey != null && hasKey) {
            redisTemplate.boundValueOps(key).set(online);
        } else {
            redisTemplate.boundValueOps(key).set(online, loginExpireTime, TimeUnit.SECONDS);
        }
    }

    @Override
    public void delOnline(String token) {
        String key = String.format(RedisKey.ONLINE_TOKEN, token);
        redisTemplate.delete(key);
    }
}

