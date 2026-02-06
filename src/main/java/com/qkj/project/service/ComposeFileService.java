package com.qkj.project.service;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;

/**
 * @author KeJiang Qi
 * @date 2025/10/27 - 17:21
 * @description 组合文件服务接口
 */
public interface ComposeFileService {

     /**
     * 初始化组合文件上传
     * @param md5 文件MD5
     * @param totalSize 文件总大小
     * @param chunkSize 分片大小
     * @param chunkNum 分片数量
     * @param fileUrl 文件URL
     * @param fileName 文件名
      * @param reUpload 是否重新上传
     * @return 初始化结果
     */
    Map<String, Object> init(String md5, Long totalSize,
                             Integer chunkSize, Integer chunkNum,
                             String fileUrl, String fileName, boolean reUpload) throws Exception;

    /**
     * 合并组合文件上传
     * @param md5 文件MD5
     * @param objectName s3对象名
     * @param chunkNum 分片数量
     */
    void merge(String md5, String objectName, Integer chunkNum) throws Exception;

    /**
     * 获取组合文件缺失分片编号
     * @param md5 文件MD5
     * @param chunkNum 分片数量
     * @param chunkSize 分片大小
     * @return 缺失分片编号列表
     */
    List<Integer> missing(String md5, Integer chunkNum, Long chunkSize) throws Exception;

    /**
     * 组合文件分片上传流
     * @param objectName s3对象名
     * @param request 请求流
     * @throws Exception 上传异常
     */
    void uploadStream(String objectName, HttpServletRequest request) throws Exception;
}
