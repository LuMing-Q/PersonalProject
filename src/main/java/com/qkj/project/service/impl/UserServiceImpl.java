package com.qkj.project.service.impl;

import com.qkj.project.common.enumerations.StatusCode;
import com.qkj.project.common.exception.BusinessException;
import com.qkj.project.dao.UserDao;
import com.qkj.project.entity.User;
import com.qkj.project.service.UserService;
import com.qkj.project.utils.BaseUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Map;

/**
 * @author KeJiang Qi
 * @date 2024/8/23 - 15:18
 * @description
 */
@Slf4j
@Service
public class UserServiceImpl implements UserService {

    @Resource
    private UserDao userDao;

    @Override
    public User userCheck(Map<String, String> params) {
        if (params == null || params.isEmpty()) {
            throw BusinessException.of(StatusCode.CODE_400, "请填写用户名、密码等认证信息");
        }
        if (!params.containsKey("username") || !params.containsKey("password")) {
            throw BusinessException.of(StatusCode.CODE_400, "用户名或密码不能为空");
        }
        // 校验用户名密码
        String username = params.get("username");
        // 前期使用
        String password = params.get("password");
        /**
         * todo 后期前端调用时使用 AES 加密
         */
        // String password = BaseUtil.base64decode(BaseUtil.aesDecrypt(params.get("password"), SystemConstant.PASS_SECRET), StandardCharsets.UTF_8);
        User user = userDao.selectUserByUsername(username);
        if (user == null) {
            throw BusinessException.of(StatusCode.CODE_402, "用户 " + username + " 不存在");
        }
        if (!user.isStatus()) {
            throw BusinessException.of(StatusCode.CODE_402, "用户 " + username + " 已禁用，如需使用请联系管理员");
        }
        if (!user.getPassword().equals(password)) {
            log.info("user.pass = {} and pass = {}", user.getPassword(), password);
            throw BusinessException.of(StatusCode.CODE_402, "密码错误");
        }
        if (BaseUtil.isEmpty(user.getRoleId())) {
            throw BusinessException.of(StatusCode.CODE_410, "用户暂未分配角色权限，请联系管理员分配对应的角色权限");
        }
        user.clear();
        return user;
    }
}
