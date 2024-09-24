package com.qkj.project.dao;

import com.qkj.project.entity.User;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * @author KeJiang Qi
 * @date 2024/8/20 - 16:23
 * @description
 */
@Mapper
public interface UserDao {
    /**
     * 获取用户信息
     * @param userId
     * @return
     */
    User selectLoginById(@Param("userId") String userId);

    /**
     * 通过用户名查询用户
     * @param username
     * @return
     */
    User selectUserByUsername(@Param("username") String username);
}
