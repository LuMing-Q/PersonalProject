package com.qkj.project.service;

/**
 * @author KeJiang Qi
 * @date 2024/12/5 - 11:48
 * @description
 */
public interface MonitorService {
    /**
     * 获取 CPU 负载数据
     * @return
     */
    double[] getCpuLoad();
}
