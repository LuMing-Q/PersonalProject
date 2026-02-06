package com.qkj.project.service.impl;

import com.qkj.project.common.enumerations.StatusCode;
import com.qkj.project.common.exception.BusinessException;
import com.qkj.project.config.MinioCfg;
import com.qkj.project.entity.FileUpload;
import com.qkj.project.service.FileService;
import com.qkj.project.utils.BaseUtil;
import io.minio.*;
import io.minio.errors.ErrorResponseException;
import io.minio.errors.MinioException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Map;

/**
 * @author KeJiang Qi
 * @date 2024/11/4 - 16:17
 * @description 文件上传实现类
 */
@Slf4j
@Service
public class FileServiceImpl implements FileService {

    @Resource(name = "minio")
    private MinioClient minioClient;

    @Resource
    private MinioCfg minioCfg;

    /**
     * 处理文件上传和其他参数请求
     * @param request 包含普通参数和文件参数的Map
     * @return 处理结果
     */
    @Override
    public String complexFileUpload(Map<String, Object> request) {
        request.forEach((key, value) -> {
            if (value instanceof MultipartFile) {
                MultipartFile file = (MultipartFile) value;
                // 文件上传操作
                String fileName = file.getOriginalFilename();
                System.out.println(fileName);
            }
        });
        //文件以及数据处理操作
        return "上传成功";
    }

    @Override
    public FileUpload upload(MultipartFile file, String fileUrl) {
        BaseUtil.folderPathValidator(fileUrl);
        BaseUtil.fileNameValidator(file.getOriginalFilename());
        String objectName;
        if (BaseUtil.isEmpty(fileUrl)) {
            objectName = file.getOriginalFilename();
        } else {
            objectName = fileUrl + "/" +
                    LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")) + "/" +
                    file.getOriginalFilename();
        }
        String path = uploadFile(file, objectName);
        log.info("文件已上传到 MinIO: {}", path);
        long filesize = file.getSize();
        String filetype = file.getContentType();
        return new FileUpload().
                setId(BaseUtil.uuid()).
                setFileName(file.getOriginalFilename()).
                setObjectName(objectName).
                setFileSize(filesize).
                setFileType(filetype).
                setFileUrl(path);
    }

    /**
     * 查看minio是否已经存在文件
     * @param objectName 文件s3路径
     * @return 是否存在
     */
    @Override
    public boolean checkFileExists(String objectName) {
        try {
            minioClient.statObject(StatObjectArgs.builder().
                    bucket(minioCfg.getBucketName()).
                    object(objectName).
                    build());
            return true;
        } catch (ErrorResponseException e) {
            return false;
        } catch (Exception e) {
            log.error("检查文件是否存在失败", e);
            return false;
        }
    }

    @Override
    public String deleteFile(String objectName) {
        try {
            minioClient.removeObject(RemoveObjectArgs.builder().
                    bucket(minioCfg.getBucketName()).
                    object(objectName).
                    build());
            return "删除成功";
        } catch (Exception e) {
            log.error("删除文件失败", e);
            return "删除失败";
        }
    }

    /**
     * 获取文件内容
     * @param objectName 对象名
     * @param view 是否预览 true 预览 false 下载
     * @param response 响应
     */
    @Override
    public void getFileContent(String objectName, Boolean view, HttpServletResponse response) {
        if (BaseUtil.isEmpty(objectName)) {
            throw BusinessException.of(StatusCode.CODE_400, "s3文件路径不能为空");
        }
        String fileName = objectName.substring(objectName.lastIndexOf('/') + 1);
        String encodedFileName;
        try {
            encodedFileName = URLEncoder
                    .encode(fileName, StandardCharsets.UTF_8.name())
                    .replaceAll("\\+", "%20");
        } catch (UnsupportedEncodingException e) {
            throw BusinessException.of(StatusCode.CODE_500, "文件名编码失败");
        }
        String dispositionType = Boolean.TRUE.equals(view) ? "inline" : "attachment";
        response.setHeader(HttpHeaders.CONTENT_DISPOSITION,
                dispositionType + "; filename*=UTF-8''" + encodedFileName);
        response.setContentType(Boolean.TRUE.equals(view) ?
                determineContentType(objectName) : MediaType.APPLICATION_OCTET_STREAM_VALUE
        );
        response.setCharacterEncoding(StandardCharsets.UTF_8.name());
        try (
            InputStream inputStream = minioClient.getObject(
                    GetObjectArgs.builder()
                            .bucket(minioCfg.getBucketName())
                            .object(objectName)
                            .build()
            );
            ServletOutputStream outputStream = response.getOutputStream()
        ) {
            byte[] buffer = new byte[8192];
            int len;
            while ((len = inputStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, len);
            }
            outputStream.flush();
        } catch (ErrorResponseException e) {
            log.error("MinIO 对象不存在: {}", objectName, e);
            throw BusinessException.of(StatusCode.CODE_404, "文件不存在");
        } catch (MinioException e) {
            log.error("MinIO 读取失败: {}", objectName, e);
            throw BusinessException.of(StatusCode.CODE_500, "文件读取失败");
        } catch (IOException | InvalidKeyException | NoSuchAlgorithmException e) {
            log.error("文件流写出异常: {}", objectName, e);
            throw BusinessException.of(StatusCode.CODE_500, "文件流写出异常");
        }
    }

    /**
     * 文件上传至 minio
     * @param file 文件
     * @param objectName s3 路径
     * @return 上传结果
     */
    private String uploadFile(MultipartFile file, String objectName) {
        try (InputStream inputStream = file.getInputStream()) {
            boolean exists = minioClient.bucketExists(BucketExistsArgs.builder().bucket(minioCfg.getBucketName()).build());
            if (!exists) {
                throw BusinessException.of(StatusCode.CODE_400, "桶" + minioCfg.getBucketName() + "不存在");
            }
            ObjectWriteResponse response = minioClient.putObject(PutObjectArgs.builder().
                    bucket(minioCfg.getBucketName()).
                    object(objectName).
                    contentType(file.getContentType()).
                    stream(inputStream, file.getSize(), -1).
                    build());
            return response.etag();
        } catch (Exception e) {
            log.error("❌ 文件上传失败: {}", objectName, e);
            throw BusinessException.of(StatusCode.CODE_400, "文件上传失败: " + e.getMessage());
        }
    }

    /**
     * 文件类型处理，根据文件名后缀确定MIME类型
     * @param objectName 文件名
     * @return MIME 类型字符串
     */
    private String determineContentType(String objectName) {
        String ext = objectName.substring(objectName.lastIndexOf('.') + 1).toLowerCase();
        switch (ext) {
            case "pdf":
                return "application/pdf";
            case "jpg":
            case "jpeg":
                return "image/jpeg";
            case "png":
                return "image/png";
            case "gif":
                return "image/gif";
            case "mp4":
                return "video/mp4";
            case "docx":
                return "application/vnd.openxmlformats-officedocument.wordprocessingml.document";
            case "xlsx":
                return "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet";
            case "csv":
                return "text/csv";
            default:
                return "application/octet-stream";
        }
    }
}
