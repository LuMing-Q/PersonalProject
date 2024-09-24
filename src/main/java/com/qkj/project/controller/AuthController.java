package com.qkj.project.controller;

import com.qkj.project.common.Online;
import com.qkj.project.common.enumerations.StatusCode;
import com.qkj.project.common.exception.BusinessException;
import com.qkj.project.service.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

/**
 * @author KeJiang Qi
 * @date 2024/8/22 - 10:46
 * @description
 */
@RestController
public class AuthController {

    /**
     * 如果配置了 auth.type=redis，则注入的值将是 redisAuthService。如果没有配置 auth.type，则注入的值将是 jwtAuthService
     */
    @Value("${auth.type:jwt}AuthService")
    private String authType;

    @Autowired
    private Map<String, AuthService> authServiceMap;

    @PostMapping("/login")
    public Online login(@RequestBody Map<String, String> params, HttpServletRequest request) {
        return authServiceMap.get(authType).login(params);
    }

    @PostMapping("/logout")
    public String login() {
        authServiceMap.get(authType).logout();
        return "退出成功";
    }
}
