package com.qkj.project.dao;

import com.qkj.project.entity.UploadedFile;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @author KeJiang Qi
 * @date 2025/9/9 - 16:46
 * @description 分片文件状态数据交互
 */
@Mapper
public interface UploadedFileDao {

    /**
     * 获取文件夹/文件状态
     * @return 状态
     */
    List<UploadedFile> findAll();

    /**
     * 获取文件上传状态
     * @param path 路径
     * @param fileHash 文件hash值
     * @return 文件详情
     */
    UploadedFile findByPathAndHash(@Param("path") String path, @Param("fileHash") String fileHash);

    /**
     * 保存文件
     * @param file 文件
     * @return 影响条数
     */
    Integer save(UploadedFile file);

    /**
     * 通过 fileHash 获取上传文件详情
     * @param fileHash 文件 hash 值
     * @return 上传文件详情
     */
    UploadedFile findByFileHash(@Param("fileHash") String fileHash);
}
