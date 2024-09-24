package com.qkj.project.service.impl;

import cn.hutool.core.convert.NumberWithFormat;
import cn.hutool.crypto.SecureUtil;
import cn.hutool.jwt.JWT;
import cn.hutool.jwt.JWTUtil;
import cn.hutool.jwt.signers.JWTSigner;
import cn.hutool.jwt.signers.JWTSignerUtil;
import com.qkj.project.common.Online;
import com.qkj.project.common.RequestHolder;
import com.qkj.project.common.constant.SystemConstant;
import com.qkj.project.common.enumerations.StatusCode;
import com.qkj.project.common.exception.BusinessException;
import com.qkj.project.dao.UserDao;
import com.qkj.project.entity.User;
import com.qkj.project.service.AuthService;
import com.qkj.project.service.UserService;
import com.qkj.project.utils.BaseUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.nio.charset.StandardCharsets;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Date;
import java.util.Map;

/**
 * @author KeJiang Qi
 * @date 2024/8/20 - 15:20
 * @description jwt 方式校验
 */
@Slf4j
@Service("jwtAuthService")
public class JwtAuthServiceImpl implements AuthService {

    @Value("${auth.public-key}")
    private String publicKey;

    @Value("${auth.private-key}")
    private String privateKey;

    @Resource
    private UserDao userDao;

    @Resource
    private UserService userService;

    @Override
    public Online login(Map<String, String> params) {
        User user = userService.userCheck(params);
        PKCS8EncodedKeySpec spec = new PKCS8EncodedKeySpec(BaseUtil.base64decode(privateKey));
        PrivateKey rsa = SecureUtil.generatePrivateKey("RSA", spec);
        JWTSigner signer = JWTSignerUtil.rs256(rsa);
        // 生成JWT Token
        String token = JWT.create()
                .setPayload("uid", user.getId())
                .setPayload("username", user.getUsername())
                .setPayload("email", user.getEmail())
                .setPayload("phone", user.getPhone())
                .setIssuedAt(new Date())
                // 1 hour expiration
                .setExpiresAt(new Date(System.currentTimeMillis() + 3600000))
                // 使用私钥签名
                .setSigner(signer)
                .sign();
        Online online = new Online();
        online.setToken(token);
        online.setUser(user);
        return online;
    }

    @Override
    public void logout() {
        /**
         * jwt 生成 token 超时后失效，所以不需要退出登录
         * todo 如果必须要退出可以将 userId和online 存储到 redis,退出时再将此项信息删除
         */
    }

    /**
     * jwt token解析
     * @param token
     * @return 1-Token 格式异常,2-Token 解析失败,3-Token 已过期,4-用户不存在
     */
    public int verify(String token) {
        token = token.replace("Bearer ", "");
        String[] parts = token.split("\\.");
        if (parts.length != 3) {
            return 1;
        }
        X509EncodedKeySpec spec = new X509EncodedKeySpec(BaseUtil.base64decode(publicKey));
        PublicKey rsa = SecureUtil.generatePublicKey("RSA", spec);
        JWTSigner signer = JWTSignerUtil.rs256(rsa);
        if (!JWTUtil.verify(token, signer)) {
            return 2;
        }
        JWT jwt = JWTUtil.parseToken(token);
        NumberWithFormat exp = (NumberWithFormat) jwt.getPayload("exp");
        if (System.currentTimeMillis() / 1000 >= exp.longValue()) {
            return 3;
        }
        String userId = (String) jwt.getPayload("uid");
        User user = userDao.selectLoginById(userId);
        if (null != user) {
            user.clear();
            RequestHolder.Value value = RequestHolder.get();
            Online online = new Online(token, user);
            value.setOnline(online);
            RequestHolder.add(value);
            return 0;
        }
        return 4;
    }
}
