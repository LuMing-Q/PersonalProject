package com.qkj.project.service;

import com.qkj.project.entity.FileUpload;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.util.Map;

/**
 * @author KeJiang Qi
 * @date 2024/11/4 - 9:11
 * @description 文件上传接口
 */
public interface FileService {

    /**
     * 复杂数据以及多文件上传
     * @param request 请求入参
     * @return 响应结果
     */
    String complexFileUpload(Map<String, Object> request);

    /**
     * 文件上传
     * @param file 文件
     * @param fileUrl 文件路径
     */
    FileUpload upload(MultipartFile file, String fileUrl);

    /**
     * 查看minio是否已经存在文件
     * @param objectName 文件s3路径
     * @return 是否存在
     */
    boolean checkFileExists(String objectName);

    /**
     * 删除附件
     * @param objectName s3全路径
     * @return 删除结果
     */
    String deleteFile(String objectName);

    /**
     * 获取文件内容
     * @param objectName s3全路径
     * @param view 是否预览 true 预览 false 下载
     * @param response 响应体
     */
    void getFileContent(String objectName, Boolean view, HttpServletResponse response);
}
