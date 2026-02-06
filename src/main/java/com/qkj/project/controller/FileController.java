package com.qkj.project.controller;

import com.qkj.project.entity.FileUpload;
import com.qkj.project.service.impl.FileServiceImpl;
import com.qkj.project.utils.BaseUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.Map;

/**
 * @author KeJiang Qi
 * @date 2024/11/1 - 10:52
 * @description 文件相关控制类
 *
 */
@Slf4j
@RestController
@RequestMapping("/file")
public class FileController {

    @Resource
    private FileServiceImpl fileService;

    @PostMapping
    public String complexFileUpload(@RequestParam Map<String, Object> common,
                                    @RequestParam Map<String, MultipartFile> files) {
        Map<String, Object> request = new HashMap<>(20);
        request.putAll(common);
        request.putAll(files);
        return fileService.complexFileUpload(request);
    }

    @PostMapping("/upload")
    public FileUpload upload(@RequestParam("file") MultipartFile file,
                             @RequestParam(value = "file_url", required = false) String fileUrl) {
        return fileService.upload(file, fileUrl);
    }

    @GetMapping("/exists")
    public boolean checkFileExists(@RequestParam(value = "file_name") String fileName,
                                   @RequestParam(value = "file_url", required = false) String fileUrl) {
            String objectName = (BaseUtil.nonEmpty(fileUrl))
                    ? fileUrl + "/" + fileName
                    : fileName;
            return fileService.checkFileExists(objectName);
    }

    @DeleteMapping
    public String deleteFile(@RequestParam(value = "object_name", required = false) String objectName) {
        return fileService.deleteFile(objectName);
    }


    @GetMapping("/file_content")
    public void getFileContent(@RequestParam(value = "object_name") String objectName,
                               @RequestParam(value = "view", required = false, defaultValue = "true") Boolean view,
                               HttpServletResponse response) {
        fileService.getFileContent(objectName, view, response);
    }
}

