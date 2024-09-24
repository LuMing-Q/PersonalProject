package com.qkj.project.utils;

import java.security.*;
import java.util.Base64;

/**
 * @author KeJiang Qi
 * @date 2024/8/22 - 9:04
 * @description 公司密钥对生成
 */
public class PublicPrivateKeyPairGenerate {

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
}
