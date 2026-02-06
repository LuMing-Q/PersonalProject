package com.qkj.project.controller;

import com.qkj.project.service.ComposeFileService;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;

/**
 * @author KeJiang Qi
 * @date 2025/10/27 - 17:03
 * @description 组合文件相关控制类
 */
@RestController
@RequestMapping("/compose")
public class ComposeFileController {

    @Resource
    private ComposeFileService composeFileService;

    @GetMapping("/init")
    public Map<String, Object> init(@RequestParam String md5,
                                    @RequestParam(value = "total_size") Long totalSize,
                                    @RequestParam(value = "chunk_size") Integer chunkSize,
                                    @RequestParam(value = "chunk_num") Integer chunkNum,
                                    @RequestParam(value = "file_url", required = false) String fileUrl,
                                    @RequestParam(value = "file_name") String fileName,
                                    @RequestParam(value = "re_upload", defaultValue = "false") boolean reUpload) throws Exception {
        return composeFileService.init(md5, totalSize, chunkSize, chunkNum, fileUrl, fileName, reUpload);
    }

    @PostMapping("/merge")
    public void merge(@RequestParam String md5,
                      @RequestParam(value = "object_name") String objectName,
                      @RequestParam(value = "chunk_num") Integer chunkNum) throws Exception {
        composeFileService.merge(md5, objectName, chunkNum);
    }

    @GetMapping("/missing")
    public List<Integer> missing(@RequestParam String md5,
                                 @RequestParam(value = "chunk_num") Integer chunkNum,
                                 @RequestParam(value = "chunk_size") Long chunkSize) throws Exception {
        return composeFileService.missing(md5, chunkNum, chunkSize);
    }

    @PostMapping("/upload/stream")
    public void uploadStream(@RequestParam(value = "object_name") String objectName,
                             HttpServletRequest request
    ) throws Exception {
        composeFileService.uploadStream(objectName, request);
    }
}
