package com.qkj.project.entity;

import lombok.Data;
import lombok.experimental.Accessors;

/**
 * @author KeJiang Qi
 * @date 2025/9/8 - 15:13
 * @description 文件上传实体类
 */
@Data
@Accessors(chain = true)
public class FileUpload {
    /**
     * 文件标识
     */
    private String  id;

    /**
     * s3存储路径
     */
    private String objectName;

    /**
     * 文件名称
     */
    private String fileName;

    /**
     * 文件大小，按字节计算
     */
    private Long fileSize;

    /**
     * 文件类型
     */
    private String fileType;

    /**
    * 文件s3路径
     */
    private String fileUrl;
}
