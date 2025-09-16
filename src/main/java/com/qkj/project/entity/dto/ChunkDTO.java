package com.qkj.project.entity.dto;

import lombok.Data;

/**
 * @author KeJiang Qi
 * @date 2025/9/9 - 14:10
 * @description 分片上传实体类
 */
@Data
public class ChunkDTO {
    private String fileUrl;
    private String fileName;
    private String fileHash;
    private Integer totalChunks;
}

