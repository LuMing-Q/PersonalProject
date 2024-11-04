package com.qkj.project.service.impl;

import com.qkj.project.common.enumerations.StatusCode;
import com.qkj.project.common.exception.BusinessException;
import com.qkj.project.service.FileService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.InputStream;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

/**
 * @author KeJiang Qi
 * @date 2024/11/4 - 16:17
 * @description
 */
@Slf4j
@Service
public class FileServiceImpl implements FileService {

    @Override
    public String complexFileUpload(Map<String, Object> request) {
        //文件以及数据处理操作
        return "上传成功";
    }

    @Override
    public void upload(MultipartFile file, String fileName, HttpServletResponse response) {
        Map<String, Object> result = new HashMap<>();
        result.put("file", file);
        result.put("fileName", fileName);
        // 后期替换为文件上传操作
        try {
            // 获取文件流
            InputStream outfile = file.getInputStream();
            response.reset();
            response.setHeader("Content-Disposition", "attachment;filename=" +
                    URLEncoder.encode(fileName.substring(fileName.lastIndexOf("/") + 1), "utf-8"));
            response.setContentType("application/octet-stream");
            response.setCharacterEncoding("UTF-8");
            response.addHeader("Access-Control-Allow-Origin", "*");
            response.addHeader("Access-Control-Allow-Headers", "*");
            response.addHeader("Access-Control-Allow-Methods", "*");
            // 获取输出流
            ServletOutputStream servletOutputStream = response.getOutputStream();
            int len;
            byte[] buffer = new byte[1024];
            while ((len = outfile.read(buffer)) > 0) {
                servletOutputStream.write(buffer, 0, len);
            }
            servletOutputStream.flush();
            outfile.close();
            servletOutputStream.close();
            log.info("文件 {} 下载成功", fileName);
        } catch (Exception e) {
            log.error("文件: " + fileName + " 下载异常: " + e);
            throw BusinessException.of(StatusCode.CODE_400, "文件: " + fileName + " 下载异常: " + e);
        }
    }
}
