package com.qkj.project.service.impl;

import com.qkj.project.cache.OnlineCache;
import com.qkj.project.common.Online;
import com.qkj.project.common.RequestHolder;
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
 * @description
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

    /**
     * redis token解析
     * @param token
     * @return 1-Token 格式异常,2-Token 解析失败,3-Token 已过期,4-用户不存在
     */
    @Override
    public int verify(String token) {
        token = token.replace("Bearer ", "");
        Online online = onlineCache.getOnline(token);
        if (null == online) {
            return 3;
        }
        User user = online.getUser();
        if (null != user) {
            return 0;
        }
        return 4;
    }
}
