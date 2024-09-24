package com.qkj.project.service;

import com.qkj.project.entity.User;

import java.util.Map;

/**
 * @author KeJiang Qi
 * @date 2024/8/23 - 15:14
 * @description 用户相关业务类
 */
public interface UserService {
    /**
     * 用户登录相关校验
     * @param params
     */
    User userCheck(Map<String, String> params);

}
