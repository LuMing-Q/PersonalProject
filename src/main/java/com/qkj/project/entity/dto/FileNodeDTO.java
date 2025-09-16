package com.qkj.project.entity.dto;

import com.google.gson.Gson;
import lombok.Data;

import java.util.List;

/**
 * @author KeJiang Qi
 * @date 2025/9/9 - 16:35
 * @description 文件夹构建实体类
 */
@Data
public class FileNodeDTO {
    private String id;
    private String fileHash;
    private String fileName;
    private String path;
    private Long size;
    private List<Integer> uploadedChunks;
    /**
     * 文件状态 waiting/uploading/done
     */
    private String status;
    private Boolean folder;
    private String parentPath;

    @Override
    public String toString() {
        return new Gson().toJson(this);
    }
}

