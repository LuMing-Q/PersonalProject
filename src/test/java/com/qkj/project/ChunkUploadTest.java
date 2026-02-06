package com.qkj.project;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.core.ParameterizedTypeReference;

import java.io.File;
import java.io.FileInputStream;
import java.security.MessageDigest;
import java.util.*;

/**
 * @author KeJiang Qi
 * @date 2025/11/21 - 16:17
 * @description
 */
@Slf4j
public class ChunkUploadTest {
    private static final String BUCKET = "yth-wdis";
    private static final int CHUNK_SIZE = 40 * 1024 * 1024;

    // 这里使用 axios 风格客户端
    private final AxiosClient axios = new AxiosClient("http://127.0.0.1:8080/file/");

    @Test
    public void testChunkUpload() throws Exception {

        File file = new File("/Users/qikejiang/Desktop/20251121/usp20251121.tar");
        long fileSize = file.length();
        int chunkNum = (int) Math.ceil((double) fileSize / CHUNK_SIZE);

        // 1. 计算 MD5
        String md5 = calcMD5(file);

        log.info("文件 MD5: {}", md5);
        LinkedHashMap<String, Object> initParam = new LinkedHashMap<String, Object>() {{
            put("md5", md5);
            put("fileSize", fileSize);
            put("chunkSize", CHUNK_SIZE);
            put("chunkNum", chunkNum);
            put("fileUrl", "/test-20251121/");
            put("fileName", file.getName());
            put("reupload", false);
            put("bucketName", BUCKET);
        }};
        // 2. init
        Map<String, Object> init = axios.get("init", initParam,Map.class);
        if ((boolean) init.get("done")) {
            log.info("服务器返回：文件秒传成功");
            return;
        }
        String objectName = init.get("objectName").toString();
        LinkedHashMap<String, Object> missingParam = new LinkedHashMap<String, Object>() {{
            put("md5", md5);
            put("chunkNum", chunkNum);
            put("chunkSize", CHUNK_SIZE);
            put("bucketName", BUCKET);
        }};
        // 3. 获取缺失分片
        List<Integer> missing = axios.get("missing", missingParam, new ParameterizedTypeReference<List<Integer>>() {});
        log.info("缺失分片：{}", missing);
        // 4. 上传缺失分片
        try (FileInputStream fis = new FileInputStream(file)) {
            for (Integer idx : missing) {
                long start = (long) (idx - 1) * CHUNK_SIZE;
                long end = Math.min(fileSize, start + CHUNK_SIZE);
                long size = end - start;
                byte[] buf = new byte[(int) size];
                fis.getChannel().position(start);
                fis.read(buf);
                LinkedHashMap<String, Object> chuckParam = new LinkedHashMap<String, Object>() {{
                    put("object_name", "temporaryStorageFolder/" + md5 + "_part" + idx);
                    put("bucket_name", BUCKET);
                }};
                axios.postStream("upload/stream", buf, chuckParam);
                log.info("分片 {} 上传成功 ({} MB)", idx, size / 1024 / 1024);
            }
        }
        LinkedHashMap<String, Object> mergeParam = new LinkedHashMap<String, Object>() {{
            put("md5", md5);
            put("chunkNum", chunkNum);
            put("objectName", objectName);
            put("bucketName", BUCKET);
        }};
        // 5. 合并
        axios.post("merge", null, mergeParam, Map.class);
        log.info("文件 {} 上传并合并成功！", file.getName());
    }

    /**
     * 计算文件 MD5
     */
    private String calcMD5(File file) throws Exception {
        MessageDigest md = MessageDigest.getInstance("MD5");
        try (FileInputStream fis = new FileInputStream(file)) {
            byte[] buf = new byte[CHUNK_SIZE];
            int len;
            while ((len = fis.read(buf)) != -1) {
                md.update(buf, 0, len);
            }
        }
        StringBuilder sb = new StringBuilder();
        for (byte b : md.digest()) sb.append(String.format("%02x", b));
        return sb.toString();
    }
}
