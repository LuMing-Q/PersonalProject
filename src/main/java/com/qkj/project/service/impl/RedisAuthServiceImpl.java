package com.qkj.project.service.impl;

import com.qkj.project.cache.OnlineCache;
import com.qkj.project.common.Online;
import com.qkj.project.common.RequestHolder;
import com.qkj.project.common.enumerations.VerifyResult;
import com.qkj.project.dao.UserDao;
import com.qkj.project.entity.User;
import com.qkj.project.service.AuthService;
import com.qkj.project.service.UserService;
import com.qkj.project.utils.BaseUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Map;

/**
 * @author KeJiang Qi
 * @date 2024/8/23 - 11:41
 * @description Redis 认证服务实现类
 */
@Slf4j
@Service("redisAuthService")
public class RedisAuthServiceImpl implements AuthService {

    @Resource
    private UserDao userDao;

    @Resource
    private UserService userService;

    @Resource
    private OnlineCache onlineCache;

    @Override
    public Online login(Map<String, String> params) {
        User user = userService.userCheck(params);
        String token = BaseUtil.uuid();
        Online online = new Online();
        online.setToken(token);
        online.setUser(user);
        onlineCache.cacheOnline(token, online);
        return online;
    }

    @Override
    public void logout() {
        RequestHolder.Value value = RequestHolder.get();
        onlineCache.delOnline(value.getToken());
    }

    @Override
    public VerifyResult verify(String token) {
        token = token.replace("Bearer ", "");
        Online online = onlineCache.getOnline(token);
        if (null == online) {
            return VerifyResult.EXPIRED;
        }
        User user = online.getUser();
        if (null != user) {
            return VerifyResult.SUCCESS;
        }
        return VerifyResult.USER_NOT_FOUND;
    }
}
