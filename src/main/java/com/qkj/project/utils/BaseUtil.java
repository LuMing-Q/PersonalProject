package com.qkj.project.utils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.qkj.project.common.enumerations.StatusCode;
import com.qkj.project.common.exception.BusinessException;
import lombok.extern.slf4j.Slf4j;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.Base64;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * 工具类
 */
@Slf4j
public class BaseUtil {
    /**
     * 获取uuid，去除-，专程小写
     * @return {@link String} uuid
     */
    public static String uuid() {
        return UUID.randomUUID().toString().replaceAll("-", "").toLowerCase();
    }

    /**
     * 判断str是否为null或者是空字符串
     * @param str 判断对象
     * @return {@link Boolean} 判断结果 null 或者 ""的时候返回true 否则返回false
     */
    public static boolean isEmpty(String str) {
        return str == null || "".equals(str);
    }

    /**
     * 判断str是否不为null或者不是空字符串
     * @param str 判断对象
     * @return {@link Boolean} 判断结果 null 或者 ""的时候返回false 否则返回true
     */
    public static boolean nonEmpty(String str) {
        return !isEmpty(str);
    }

    /**
     * 做参数转化，Map格式的参数转化成字符串参数
     * <p>例如 {"a":"b","c":"d"} 转成 a=b&c=d</p>
     * @param json 参数
     * @return {@link String} 字符串参数
     */
    public static String paramsConversion(Map<String, Object> json) {
        return json.entrySet().stream().map(e -> e.getKey() + "=" + e.getValue())
                .collect(Collectors.joining("&"));
    }

    /**
     * 反序列化成 {JSONObject} 对象
     * 如果参数为空，则返回空对象{}
     * @param str json字符串
     * @return {@link JSONObject}
     */
    public static JSONObject parseJson(String str) {
        return isEmpty(str) ? new JSONObject() : JSON.parseObject(str);
    }

    /**
     * 生成JSONArray并且加入元素e
     * @param e 元素
     * @return {@link JSONArray} 生成的数组
     */
    public static JSONArray as(Object... e) {
        JSONArray j = new JSONArray();
        j.addAll(Arrays.asList(e));
        return j;
    }

    /**
     * 生成JSONObject并且加入元素k,v
     * @param k 元素key
     * @param v 元素value
     * @return {@link JSONObject} 生成的对象
     */
    public static JSONObject asJSONObject(String k, Object v) {
        JSONObject j = new JSONObject();
        j.put(k, v);
        return j;
    }

    /**
     * Base64加密
     */
    public static String base64encode(String src) {
        return Base64.getEncoder().encodeToString(src.getBytes(StandardCharsets.UTF_8));
    }

    /**
     * Base64加密
     */
    public static String base64encode(byte[] bytes) {
        return Base64.getEncoder().encodeToString(bytes);
    }

    /**
     * Base64解密
     */
    public static String base64decode(String src, Charset charset) {
        return new String(Base64.getDecoder().decode(src), charset);
    }

    /**
     * Base64解密
     */
    public static byte[] base64decode(String str) {
        return Base64.getDecoder().decode(str);
    }

    /**
     * URL编码
     */
    public static String urlEncode(String src) {
        try { return URLEncoder.encode(src, "UTF-8"); }
        catch (UnsupportedEncodingException e) {
            log.error("URL encode error: {}", e.getMessage());
            throw BusinessException.of(StatusCode.CODE_504, "URL encode失败");
        }
    }

    /**
     * URL解码
     */
    public static String urlDecode(String str) {
        try { return URLDecoder.decode(str, "UTF-8"); }
        catch (UnsupportedEncodingException e) {
            log.error("URL decode error: {}", e.getMessage());
            throw BusinessException.of(StatusCode.CODE_504, "URL decode失败");
        }
    }

    /**
     * AES加密
     */
    public static String aesEncrypt(String src, String key) {
        try {
            if (src == null || key == null) {
                return null;
            }
            Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
            cipher.init(Cipher.ENCRYPT_MODE, new SecretKeySpec(key.getBytes(StandardCharsets.UTF_8), "AES"));
            byte[] bytes = cipher.doFinal(src.getBytes(StandardCharsets.UTF_8));
            return base64encode(bytes);
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * AES解密
     */
    public static String aesDecrypt(String str, String key) {
        try {
            if (isEmpty(str)) {
                return null;
            }
            byte[] bytes = base64decode(str);
            Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
            cipher.init(Cipher.DECRYPT_MODE, new SecretKeySpec(key.getBytes(StandardCharsets.UTF_8), "AES"));
            bytes = cipher.doFinal(bytes);
            return new String(bytes, StandardCharsets.UTF_8);
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * 计算SHA256加密值
     * @param src 原文
     * @return {@link String} 密文
     */
    public static String sha256(String src) {
        if (isEmpty(src)) { return ""; }
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(src.getBytes(StandardCharsets.UTF_8));
            StringBuilder builder = new StringBuilder();
            for (byte h : hash) {
                String hex = Integer.toHexString(0xff & h);
                if (hex.length() == 1) { builder.append('0'); }
                builder.append(hex);
            }
            return builder.toString();
        } catch (NoSuchAlgorithmException e) {
            log.error("SHA-256 compute error: {}", e.getMessage());
            throw BusinessException.of(StatusCode.CODE_504, "计算SHA256出错了", e.getMessage());
        }
    }

}
