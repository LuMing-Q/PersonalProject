package com.qkj.project.service;

import com.qkj.project.entity.FileUpload;
import com.qkj.project.entity.dto.ChunkDTO;
import com.qkj.project.entity.dto.FileNodeDTO;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.util.List;
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
     * 文件夹上传
     * @param file 文件夹
     */
    void updateFolder(MultipartFile file) throws Exception;

    /**
     * 获取文件夹/文件状态
     * @return 状态
     */
    List<FileNodeDTO> getAllUploadedFileNodes();

    void saveOrUpdateFileNodes(List<FileNodeDTO> nodes);

    /**
     * 处理文件分片上传
     * @param file 文件分片
     * @param fileUrl 文件上传后的URL路径
     * @param fileHash 文件的哈希值，用于生成唯一的目录
     * @param chunkIndex 分片的索引，用于命名分片文件
     * @return 上传结果
     */
    FileUpload uploadChunk(MultipartFile file, String fileUrl, String fileHash, Integer chunkIndex);

    /**
     *
     * @param fileHash 文件的哈希值
     * @return 已上传分片
     */
    List<Integer> checkChunks(String fileHash);

    /**
     * 合并分片
     * @param chunkDTO 文件分片
     * @return 文件 s3 位置信息
     */
    FileUpload mergeChunks(ChunkDTO chunkDTO);

    /**
     * 异步删除临时分片目录
     * @param dir 文件
     */
    void deleteTempDirAsync(File dir);

    /**
     * 文件上传至 minio
     *
     * @param file       文件
     * @param objectName s3 路径
     * @return 上传结果
     */
    String uploadFile(MultipartFile file, String objectName);
}
