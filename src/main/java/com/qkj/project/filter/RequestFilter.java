package com.qkj.project.filter;

import com.alibaba.fastjson.JSON;
import com.qkj.project.common.RequestHolder;
import com.qkj.project.common.Result;
import com.qkj.project.common.enumerations.StatusCode;
import com.qkj.project.config.AuthConfig;
import com.qkj.project.service.AuthService;
import com.qkj.project.utils.BaseUtil;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Map;

/**
 * @author KeJiang Qi
 * @date 2024/8/20 - 14:57
 * @description 登录拦截检验类
 */
@Slf4j
@Component
public class RequestFilter implements Filter {

    @Resource
    private AuthConfig authConfig;

    @Value("${auth.type:default}AuthService")
    private String authType;

    @Autowired
    private Map<String, AuthService> authServiceMap;


    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain chain)
            throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) servletRequest;
        HttpServletResponse response = (HttpServletResponse) servletResponse;
        String logId = request.getHeader("LOG-ID");
        if (BaseUtil.isEmpty(logId)) { logId = BaseUtil.uuid(); }
        MDC.put("LOG_ID", logId);
        RequestHolder.Value value = new RequestHolder.Value();
        value.setLogId(logId);
        RequestHolder.add(value);
        String method = request.getMethod();
        if ("OPTIONS".equals(method)) {
            responseData(StatusCode.CODE_200.getCode(), "", response);
            return;
        }
        String uri = request.getRequestURI();
        // 不需要token校验的接口
        if (authConfig.ignoreUrlsContain(uri)) {
            chain.doFilter(servletRequest, servletResponse);
            return;
        }
        log.info("header names ===  {}, method === {}", JSON.toJSONString(request.getHeaderNames()), method);
        // token 存放在接口请求参数中的接口
        boolean b = authConfig.queryUrlsContain(uri);
        String token = b ? request.getParameter("Authorization") : request.getHeader("Authorization");
        if (BaseUtil.isEmpty(token)) {
            responseData(StatusCode.CODE_401.getCode(), "Token 不存在", response);
            return;
        }
        int verify = authServiceMap.get(authType).verify(token);
        if (verify == 1) {
            responseData(StatusCode.CODE_401.getCode(), "Token 格式异常", response);
            return;
        } else if (verify == 2) {
            responseData(StatusCode.CODE_401.getCode(), "Token 解析失败", response);
            return;
        } else if (verify == 3) {
            responseData(StatusCode.CODE_401.getCode(), "Token 已过期", response);
            return;
        } else if (verify == 4) {
            responseData(StatusCode.CODE_401.getCode(), "用户不存在", response);
            return;
        }
        value.setToken(token);
        log.info("Authorization  = {}", token);
        chain.doFilter(servletRequest, servletResponse);
    }

    private void responseData(int code, String message, HttpServletResponse response) throws IOException {
        response.setStatus(StatusCode.CODE_200.getCode());
        response.setContentType("application/json;charset=utf8");
        PrintWriter writer = response.getWriter();
        response.addHeader("Access-Control-Allow-Origin", "*");
        response.addHeader("Access-Control-Allow-Headers", "*");
        response.addHeader("Access-Control-Allow-Methods", "*");
        writer.write(JSON.toJSONString(Result.failed(code, message)));
        writer.flush();
    }
}
