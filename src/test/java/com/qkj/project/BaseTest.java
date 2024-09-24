package com.qkj.project;

import cn.hutool.core.convert.NumberWithFormat;
import cn.hutool.crypto.SecureUtil;
import cn.hutool.jwt.JWT;
import cn.hutool.jwt.JWTUtil;
import cn.hutool.jwt.signers.JWTSigner;
import cn.hutool.jwt.signers.JWTSignerUtil;
import com.qkj.project.common.Online;
import com.qkj.project.common.RequestHolder;
import com.qkj.project.entity.User;
import com.qkj.project.utils.BaseUtil;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

import java.security.*;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;
import java.util.Date;

/**
 * @author KeJiang Qi
 * @date 2024/8/22 - 9:24
 * @description
 */
@Slf4j
public class BaseTest {

    /**
     * 密钥对生成
     */
    @Test
    public void generate() {
        try {
            // 创建KeyPairGenerator对象，并指定RSA算法
            KeyPairGenerator keyPairGen = KeyPairGenerator.getInstance("RSA");

            // 初始化KeyPairGenerator，指定密钥长度（2048位）
            keyPairGen.initialize(512);

            // 生成密钥对
            KeyPair pair = keyPairGen.generateKeyPair();

            // 获取公钥和私钥
            PublicKey publicKey = pair.getPublic();
            PrivateKey privateKey = pair.getPrivate();

            // 将公钥和私钥编码为Base64格式
            String publicKeyBase64 = Base64.getEncoder().encodeToString(publicKey.getEncoded());
            String privateKeyBase64 = Base64.getEncoder().encodeToString(privateKey.getEncoded());

            // 输出公钥和私钥
            System.out.println("Public Key: " + publicKeyBase64);
            System.out.println("Private Key: " + privateKeyBase64);

        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
    }

    /**
     * token 生成
     */
    @Test
    public void generateTest() {
        PKCS8EncodedKeySpec spec = new PKCS8EncodedKeySpec(BaseUtil.base64decode("MIIBVQIBADANBgkqhkiG9w0BAQEFAASCAT8wggE7AgEAAkEAiYhC9kdk/eOPgncQsotuep/gkxNP9OxpsbuKK1v/nbSiE7IGeI9nKuj0K3Zl05LEtWYHSsLu6l2A5jspVWt9XQIDAQABAkBylsGUCPAwSYWsoh+bY2jtan/mitS3sLJvj14TuldDwZorAryUYjJk2lulA+bUA7tsRz2ia6BAjYF5cXtIS2eBAiEA47cG92y5UDM5UYu9m4iEQU5B+fwY4zyRNfqLZTZPpPUCIQCanZGc0xhGh7UIqbz5EGdGXHUwKWVNdtyUZo/+qSN1yQIgZ4FBJGQCE6dE7YEELVcvLnQA3z0sntVf99YrWFUIfKUCIQCFGN8ggReEn43Xx0ZFoTlF+JFoxDnQVWHM3f1W24MLAQIhAMDcy6L+WYnZOqm7n/A7hlPN9H4E/ggw43PcIzyBdv8v"));
        PrivateKey rsa = SecureUtil.generatePrivateKey("RSA", spec);
        JWTSigner signer = JWTSignerUtil.rs256(rsa);
        // 生成JWT Token
        String token = JWT.create()
                .setPayload("uid", "1234")
                .setPayload("username", "zhangsan")
                .setIssuedAt(new Date())
                // 1 hour expiration
                .setExpiresAt(new Date(System.currentTimeMillis() + 3600000))
                // 使用私钥签名
                .setSigner(signer)
                .sign();
        log.info("token = {}", token);
    }

    /**
     * token 解析
     */
    @Test
    public void verify() {
        X509EncodedKeySpec spec = new X509EncodedKeySpec(BaseUtil.base64decode("MFwwDQYJKoZIhvcNAQEBBQADSwAwSAJBAI71DbL/2sMTAmP22jdlhhU9TiVHjWr3I/kDTX821puYX5Q6nkb9bcTi3KjIMczTbSw4Lnem0flidzBtene+ltUCAwEAAQ=="));
        PublicKey rsa = SecureUtil.generatePublicKey("RSA", spec);
        JWTSigner signer = JWTSignerUtil.rs256(rsa);
        String token = "Bearer eyJ0eXAiOiJKV1QiLCJhbGciOiJSUzI1NiJ9.eyJ1aWQiOiIxMjM0IiwidXNlcm5hbWUiOiJ6aGFuZ3NhbiIsImlhdCI6MTcyNDMxMTgzNSwiZXhwIjoxNzI0MzE1NDM1fQ.fv6taMJEL7zHHJFOIAXnqCma-7CMCw2zgc6cP4lZwZq1CgaVq4qoYODEUKUHpk_G1fujcZSU6QuVxQDzuqKrWA";
        token = token.replace("Bearer ", "");
        if (BaseUtil.nonEmpty(token)){
            token = token.replace("Bearer ", "");
            String[] parts = token.split("\\.");
            if (parts.length != 3) {
                log.info("无效的 Token");
            }
        }
        if (!JWTUtil.verify(token, signer)) {
            log.info("Token 校验失败");
        }
        JWT jwt = JWTUtil.parseToken(token);
        NumberWithFormat exp = (NumberWithFormat) jwt.getPayload("exp");
        if (System.currentTimeMillis() / 1000 >= exp.longValue()) {
            log.info("Token 已失效");
        }
        String userId = (String) jwt.getPayload("uid");
        String username = (String) jwt.getPayload("username");
        User user = new User(userId, username, "", "", "", true);
        if (null != user) {
            RequestHolder.Value value = new RequestHolder.Value();
            Online online = new Online(token, user);
            value.setOnline(online);
            RequestHolder.add(value);
            log.info("User: {}", user);
        }
    }
}
