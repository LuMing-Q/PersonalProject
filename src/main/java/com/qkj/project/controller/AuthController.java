package com.qkj.project.controller;

import com.qkj.project.common.Online;
import com.qkj.project.common.RequestHolder;
import com.qkj.project.common.annotations.ULog;
import com.qkj.project.entity.User;
import com.qkj.project.service.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/**
 * @author KeJiang Qi
 * @date 2024/8/22 - 10:46
 * @description 权限控制类
 */
@RestController
public class AuthController {

    /**
     * 如果配置了 auth.type=redis，则注入的值将是 redisAuthService。如果没有配置 auth.type，则注入的值将是 jwtAuthService
     */
    @Value("${auth.type:jwt}AuthService")
    private String authType;

    private Map<String, AuthService> authServiceMap;

    @Autowired
    private void setAuthServiceMap(Map<String, AuthService> authServiceMap) {
        this.authServiceMap = authServiceMap;
    }

    @PostMapping("/login")
    public Online login(@RequestBody Map<String, String> params) {
        return authServiceMap.get(authType).login(params);
    }

    @ULog("获取当前登录用户信息")
    @GetMapping("/login_user")
    public User getLoginUser() {
        return RequestHolder.get().getUser();
    }

    @ULog("退出登录")
    @PostMapping("/logout")
    public String login() {
        authServiceMap.get(authType).logout();
        return "退出成功";
    }
}
