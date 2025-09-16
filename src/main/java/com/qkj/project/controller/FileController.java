package com.qkj.project.controller;

import com.qkj.project.entity.FileUpload;
import com.qkj.project.entity.dto.ChunkDTO;
import com.qkj.project.entity.dto.FileNodeDTO;
import com.qkj.project.service.impl.FileServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.List;
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

    /**
     * 处理文件上传和其他参数请求
     * @param common 普通参数
     * @param files  文件参数<br>
     * <img src="https://pic.imgdb.cn/item/67248a92d29ded1a8c35eb0f.png">
     * @return 处理结果
     */
    @PostMapping
    public String complexFileUpload(@RequestParam Map<String, Object> common, @RequestParam Map<String, MultipartFile> files) {
        Map<String, Object> request = new HashMap<>();
        request.putAll(common);
        request.putAll(files);
        return fileService.complexFileUpload(request);
    }

    @PostMapping("/upload")
    public FileUpload upload(@RequestParam("file") MultipartFile file,
                             @RequestParam(value = "file_url", required = false) String fileName) {
        return fileService.upload(file, fileName);
    }

    @PostMapping("/folder")
    public void updateFolder(@RequestParam("file") MultipartFile file) throws Exception {
        fileService.updateFolder(file);
    }

    @PostMapping("/uploadChunk")
    public void uploadChunk(@RequestParam MultipartFile file,
                            @RequestParam String fileUrl,
                            @RequestParam String fileHash,
                            @RequestParam Integer chunkIndex) {
        fileService.uploadChunk(file, fileUrl, fileHash, chunkIndex);
    }

    @GetMapping("/checkChunks")
    public List<Integer> checkChunks(@RequestParam String fileHash) {
        return fileService.checkChunks(fileHash);
    }

    @PostMapping("/mergeChunks")
    public void mergeChunks(@RequestBody ChunkDTO chunkDTO) {
        fileService.mergeChunks(chunkDTO);
    }

    @PostMapping("/saveState")
    public void saveState(@RequestBody List<FileNodeDTO> nodes) {
        fileService.saveOrUpdateFileNodes(nodes);
    }

    @GetMapping("/getState")
    public List<FileNodeDTO> getState() {
        return fileService.getAllUploadedFileNodes();
    }
}

