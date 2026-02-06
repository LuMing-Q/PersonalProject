package com.qkj.project.entity;

import com.google.gson.Gson;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * @author KeJiang Qi
 * @date 2025/9/9 - 16:39
 * @description
 */
@Data
public class UploadedFile {
    private String id;
    private String fileHash;
    private String fileName;
    private String path;
    private Long size;
    private String uploadedChunks;
    private String status;
    private Boolean folder;
    private String parentPath;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;

    @Override
    public String toString() {
        return new Gson().toJson(this);
    }
}
