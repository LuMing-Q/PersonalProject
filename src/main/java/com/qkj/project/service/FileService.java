package com.qkj.project.service;

import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.util.Map;

/**
 * @author KeJiang Qi
 * @date 2024/11/4 - 9:11
 * @description
 */
public interface FileService {

    /**
     * 复杂数据以及多文件上传
     * @param request
     * @return
     */
    String complexFileUpload(Map<String, Object> request);

    void upload(MultipartFile file, String fileName, HttpServletResponse response);
}
