package com.qkj.project.service.impl;

import com.qkj.project.common.enumerations.StatusCode;
import com.qkj.project.common.exception.BusinessException;
import com.qkj.project.config.MinioCfg;
import com.qkj.project.service.ComposeFileService;
import com.qkj.project.utils.BaseUtil;
import io.minio.*;
import io.minio.errors.ErrorResponseException;
import io.minio.http.Method;
import io.minio.messages.Item;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.MediaTypeFactory;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.io.InputStream;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static java.util.regex.Pattern.compile;

/**
 * @author KeJiang Qi
 * @date 2025/10/27 - 17:22
 * @description 组合文件服务实现类
 */
@Slf4j
@Service
public class ComposeFileServiceImpl implements ComposeFileService {

    @Resource(name = "minio")
    private MinioClient minioClient;

    @Resource
    private MinioCfg minioCfg;

    /**
     * 分片数据临时存储文件夹
     */
    private final String folder = "temporaryStorageFolder/";

    @Override
    public Map<String, Object> init(String md5, Long totalSize,
                                    Integer chunkSize, Integer chunkNum,
                                    String fileUrl, String fileName, boolean reUpload) throws Exception {
        String objectName = BaseUtil.isEmpty(fileUrl) ? fileName : fileUrl + fileName;
        String prefix = md5 + "_part";
        boolean done = false;
        try {
            minioClient.statObject(StatObjectArgs.builder()
                    .bucket(minioCfg.getBucketName())
                    .object(objectName)
                    .build());
            done = true;
        } catch (ErrorResponseException e) {
            String errorKey = "NoSuchKey";
            if (!errorKey.equals(e.errorResponse().code())) {
                throw e;
            }
        }
        if (reUpload && done) {
            try {
                minioClient.removeObject(RemoveObjectArgs.builder()
                        .bucket(minioCfg.getBucketName())
                        .object(objectName)
                        .build());
                done = false;
            } catch (Exception ex) {
                log.warn("重新上传删除旧文件失败：{}", objectName, ex);
            }
        }
        Map<String, Object> resp = new HashMap<>(20);
        resp.put("done", done);
        resp.put("objectName", objectName);
        resp.put("prefix", prefix);
        resp.put("chunkSize", chunkSize);
        resp.put("chunkNum", chunkNum);
        if (done) {
            resp.put("url", objectName);
        }
        return resp;
    }

    @Override
    public void merge(String md5, String objectName, Integer chunkNum) throws Exception {
        String prefix = folder + md5 + "_part";
        // 从 MinIO 中获取所有匹配的分片对象
        Iterable<Result<Item>> results = minioClient.listObjects(
                ListObjectsArgs.builder()
                        .bucket(minioCfg.getBucketName())
                        .prefix(prefix)
                        .build());
        // 过滤并排序分片对象
        List<Item> items = new ArrayList<>();
        for (Result<Item> r : results) {
            items.add(r.get());
        }
        // 过滤出有效分片对象（分片编号大于 0）并排序
        List<ComposeSource> sources = items.stream()
                .map(item -> {
                    int idx = parsePartIndex(item.objectName());
                    return new AbstractMap.SimpleEntry<>(idx, item);
                })
                .filter(e -> e.getKey() > 0)
                .sorted(Map.Entry.comparingByKey())
                .map(e -> ComposeSource.builder()
                        .bucket(minioCfg.getBucketName())
                        .object(e.getValue().objectName())
                        .build())
                .collect(Collectors.toList());
        if (sources.size() != chunkNum) {
            throw new RuntimeException("分片数量不匹配，期望分片数：" + chunkNum + "，实际分片数：" + sources.size());
        }
        minioClient.composeObject(
                ComposeObjectArgs.builder()
                        .bucket(minioCfg.getBucketName())
                        .object(objectName)
                        .sources(sources)
                        .build()
        );
        log.info("文件合并完成: {}", objectName);
        updateContentType(minioCfg.getBucketName(), objectName);
        // 清理分片对象
        for (ComposeSource s : sources) {
            minioClient.removeObject(RemoveObjectArgs.builder()
                    .bucket(minioCfg.getBucketName())
                    .object(s.object())
                    .build());
        }
        log.info("分片已清理完成");
    }

    @Override
    public List<Integer> missing(String md5, Integer chunkNum, Long chunkSize) throws Exception {
        String prefix = folder + md5 + "_part";
        Iterable<Result<Item>> results = minioClient.listObjects(
                ListObjectsArgs.builder()
                        .bucket(minioCfg.getBucketName())
                        .prefix(prefix)
                        .build());
        Set<Integer> done = new HashSet<>();
        for (Result<Item> r : results) {
            Item item = r.get();
            String name = item.objectName();
            int idx = parsePartIndex(name);
            if (idx <= 0) {
                continue;
            }
            long size = item.size();
            if (chunkSize != null && chunkSize > 0) {
                if (idx == chunkNum) {
                    if (size > 0) {
                        done.add(idx);
                    }
                } else {
                    if (size >= chunkSize) {
                        done.add(idx);
                    }
                }
            } else {
                done.add(idx);
            }
        }
        return IntStream.rangeClosed(1, chunkNum)
                .filter(i -> !done.contains(i))
                .boxed()
                .collect(Collectors.toList());
    }

    @Override
    public void uploadStream(String objectName, HttpServletRequest request)  throws Exception {
        try (InputStream in = request.getInputStream()) {
            long size = request.getContentLengthLong();
            PutObjectArgs args = PutObjectArgs.builder()
                    .bucket(minioCfg.getBucketName())
                    .object(objectName)
                    .stream(in, size, -1)
                    .contentType("application/octet-stream")
                    .build();
            minioClient.putObject(args);
        }
    }

    private final Pattern PART_PATTERN = compile("_part(\\d+)$");

    /**
     * 从 MinIO 对象名中解析分片编号
     * 示例：
     * - ab2cd1ef123456_part1  -> 1
     * - ab2cd1ef123456_part2  -> 2
     * - 123e4567e89b12_part10 -> 10
     */
    private int parsePartIndex(String objectName) {
        if (objectName == null) {
            return -1;
        }
        Matcher matcher = PART_PATTERN.matcher(objectName);
        if (matcher.find()) {
            try {
                return Integer.parseInt(matcher.group(1));
            } catch (NumberFormatException ignored) {
                log.warn("无法解析分片索引: {}", matcher.group(1));
                throw BusinessException.of(StatusCode.CODE_400, "无法解析分片索引: " + matcher.group(1));
            }
        }
        return -1;
    }

    /**
     * 根据对象名检测内容类型
     * @param objectName 对象名
     * @return 内容类型
     */
    private String detectContentTypeByObjectName(String objectName) {
        return MediaTypeFactory
                .getMediaType(objectName)
                .map(MediaType::toString)
                .orElse(MediaType.APPLICATION_OCTET_STREAM_VALUE);
    }

    /**
     * 更新文件夹内文件类型
     * @param bucketName 桶名称
     * @param objectName s3文件全路径
     * @throws Exception 异常信息
     */
    private void updateContentType(String bucketName, String objectName) throws Exception {
        String newType = detectContentTypeByObjectName(objectName);
        StatObjectResponse stat = minioClient.statObject(
                StatObjectArgs.builder()
                        .bucket(bucketName)
                        .object(objectName)
                        .build()
        );
        String oldType = stat.contentType();
        if (newType != null && !newType.equals(oldType)) {
            Map<String, String> headers = new HashMap<>(2);
            headers.put("Content-Type", newType);
            minioClient.copyObject(
                    CopyObjectArgs.builder()
                            .bucket(bucketName)
                            .object(objectName)
                            .source(
                                    CopySource.builder()
                                            .bucket(bucketName)
                                            .object(objectName)
                                            .build()
                            )
                            .headers(headers)
                            .metadataDirective(Directive.REPLACE)
                            .build()
            );
            log.info("Content-Type 修复完成: {} -> {}", oldType, newType);
        }
    }
}
