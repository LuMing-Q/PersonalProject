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
import java.net.InetAddress;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.net.UnknownHostException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Paths;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.Base64;
import java.util.Map;
import java.util.UUID;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.zip.CRC32;

/**
 * @author KeJiang Qi
 * @date 2025/2/21 - 15:40
 * @description 基础工具类，提供一些常用的方法封装
 */
@Slf4j
public class BaseUtil {

    private static SnowflakeId snowflakeId;

    /** 允许的文件名字符：禁止 Windows 非法字符 <>:"/\|?* */
    private static final Pattern SAFE_FILE_NAME = Pattern.compile("^[^<>:\"/\\\\|?*]+$");

    /**
     * 校验多层文件夹路径
     * 允许 /文件夹1/文件夹2 格式，支持中文、空格、数字、下划线、中划线、点号
     * 自动去除首尾多余的 "/"，防止路径穿越
     */
    private static final Pattern SAFE_FOLDER_NAME = Pattern.compile("^[^<>:\"/\\\\|?*]+$");


    /**
     * 文件名/文件夹名 最大长度
     */
    private static final int MAX_FILENAME_LENGTH = 255;

    static {
        try {
            InetAddress address = InetAddress.getLocalHost();
            byte[] ip = address.toString().getBytes(StandardCharsets.UTF_8);
            CRC32 crc32 = new CRC32();
            crc32.update(ip, 0, ip.length);
            long value = crc32.getValue();
            snowflakeId = new SnowflakeId(value % 32, 31);
        } catch (UnknownHostException e) {
            log.warn("Unknown Host Exception ---- {}", e.getMessage());
            snowflakeId = new SnowflakeId(31, 30);
        }
    }

    /**
     * 获取uuid，去除-，转成小写
     * @return uuid
     */
    public static String uuid() {
        return UUID.randomUUID().toString().replaceAll("-", "").toLowerCase();
    }

    /**
     * 生成雪花Id
     * @return {@link Long}
     */
    public static long snowflakeId() {
        return snowflakeId.generate();
    }

    /**
     * 判断 str 是否为 null 或者是空字符串
     * @param str 判断对象
     * @return 判断结果 null 或者 ""的时候返回 true 否则返回 false
     */
    public static boolean isEmpty(String str) {
        return str == null || str.isEmpty();
    }

    /**
     * 判断str是否不为 null 或者不是空字符串
     * @param str 判断对象
     * @return 判断结果 null 或者 ""的时候返回 false 否则返回 true
     */
    public static boolean nonEmpty(String str) {
        return !isEmpty(str);
    }

    /**
     * 做参数转化，Map格式的参数转化成字符串参数 ===> 用于外部请求 query 参数构建
     * <p>例如 {"a":"b","c":"d"} 转成 a=b&c=d</p>
     * @param json 参数
     * @return 字符串参数
     */
    public static String paramsConversion(Map<String, Object> json) {
        return json.entrySet().stream().map(e -> e.getKey() + "=" + e.getValue())
                .collect(Collectors.joining("&"));
    }

    /**
     * 反序列化成 {JSONObject} 对象
     * 如果参数为空，则返回空对象{}
     * @param str json字符串
     * @return {@link JSONObject} 对象
     */
    public static JSONObject parseJson(String str) {
        return isEmpty(str) ? new JSONObject() : JSON.parseObject(str);
    }

    /**
     * 生成JSONArray并且加入元素e
     * @param e 元素
     * @return {@link JSONArray} 生成的对象
     */
    public static JSONArray as(Object... e) {
        JSONArray j = new JSONArray();
        j.addAll(Arrays.asList(e));
        return j;
    }

    /**
     * 生成JSONObject并且加入元素k,v
     * @param key 键
     * @param value 元素value
     * @return 生成的 JSONObject对象
     */
    public static JSONObject generateJson(String key, Object value) {
        JSONObject result = new JSONObject();
        result.put(key, value);
        return result;
    }

    /**
     * Base64加密
     * @param src 源字符串
     * @return 加密后的字符串
     */
    public static String base64encode(String src) {
        return Base64.getEncoder().encodeToString(src.getBytes(StandardCharsets.UTF_8));
    }

    /**
     * Base64加密
     * @param bytes 字节数组
     * @return 加密后的字符串
     */
    public static String base64encode(byte[] bytes) {
        return Base64.getEncoder().encodeToString(bytes);
    }

    /**
     * Base64解密
     * @param src 源字符串
     * @param charset 字符集
     * @return 解密后的字符串
     */
    public static String base64decode(String src, Charset charset) {
        return new String(Base64.getDecoder().decode(src), charset);
    }

    /**
     * Base64解密
     * @param str 源字符串
     * @return 解密后的字节数组
     */
    public static byte[] base64decode(String str) {
        return Base64.getDecoder().decode(str);
    }

    /**
     * URL编码
     * @param src 源字符串
     * @return 加密后的字符串
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
     * @param str 源字符串
     * @return 解密后的字符串
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
     * @param src 源字符串
     * @param key 密钥
     * @return 加密后的字符串
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
     * @param str 源字符串
     * @param key 密钥
     * @return 解密后的字符串
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
     * @return 密文
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

    /**
     * 生成排序字段
     * @param sort 排序字段字符串，例如：name:1,age:-1
     * @return 排序字段字符串，例如：name ASC,age DESC<br/>
     * sql编写时需要用 拼接符 # 不能使用占位符  $<br/>
     * <img src="https://pic1.imgdb.cn/item/67e0b40d88c538a9b5c54edf.png" alt="image.png">
     */
    public static String genSort(String sort) {
        if (BaseUtil.isEmpty(sort)) {
            throw BusinessException.of(StatusCode.CODE_400, "排序字段不能为空");
        }
        else {
            return sort.replaceAll(":", " ")
                    .replaceAll("-\\d+", "DESC")
                    .replaceAll("\\d+", "ASC");
        }
    }

    /**
     * 校验单个文件名
     */
    public static String fileNameValidator(String fileName) {
        if (isEmpty(fileName)) {
            throw BusinessException.of(StatusCode.CODE_400, "文件名不能为空");
        }
        // 去掉任何路径，只保留文件名
        String normalized = Paths.get(fileName).getFileName().toString();
        // 长度限制
        if (normalized.length() > MAX_FILENAME_LENGTH) {
            throw BusinessException.of(StatusCode.CODE_400, "文件名过长");
        }
        // 校验非法字符
        if (!SAFE_FILE_NAME.matcher(normalized).matches()) {
            throw BusinessException.of(StatusCode.CODE_400, "非法文件名: " + normalized);
        }
        return normalized;
    }

    public static String folderPathValidator(String folderPath) {
        if (isEmpty(folderPath)) {
            throw BusinessException.of(StatusCode.CODE_400, "文件路径不能为空");
        }
        String normalized = folderPath.replace("\\", "/");
        // 去掉首尾多余的 "/"
        if (normalized.startsWith("/")) {
            normalized = normalized.substring(1);
        }
        if (normalized.endsWith("/")) {
            normalized = normalized.substring(0, normalized.length() - 1);
        }
        // 检查路径穿越
        if (normalized.contains("..")) {
            throw BusinessException.of(StatusCode.CODE_400, "禁止路径穿越: " + normalized);
        }
        // 逐级校验文件夹名
        String[] parts = normalized.split("/");
        for (String part : parts) {
            if (isEmpty(part)) {
                throw BusinessException.of(StatusCode.CODE_400, "非法路径: " + normalized);
            }
            if (part.length() > MAX_FILENAME_LENGTH) {
                throw BusinessException.of(StatusCode.CODE_400, "文件夹名过长: " + part);
            }
            if (!SAFE_FOLDER_NAME.matcher(part).matches()) {
                throw BusinessException.of(StatusCode.CODE_400, "非法文件夹名: " + part);
            }
        }
        return normalized;
    }
}
