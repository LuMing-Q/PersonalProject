package com.qkj.project.controller;

import com.qkj.project.service.FileService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
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
@RestController
@RequestMapping("/file")
public class FileController {

    @Resource
    private FileService service;

    /**
     * 处理文件上传和其他参数请求
     * @param common 普通参数
     * @param files  文件参数<br>
     * <img src="https://pic.imgdb.cn/item/67248a92d29ded1a8c35eb0f.png">
     * @return
     */
    @PostMapping
    public String complexFileUpload(@RequestParam Map<String, Object> common, @RequestParam Map<String, MultipartFile> files) {
        Map<String, Object> request = new HashMap<>();
        request.putAll(common);
        request.putAll(files);
        return service.complexFileUpload(request);
    }

    @PostMapping("/upload")
    public void upload(@RequestParam("file") MultipartFile file,
                       @RequestParam(value = "file_url", required = false) String fileName,
                       HttpServletResponse response) {
        service.upload(file, fileName, response);
    }
}
