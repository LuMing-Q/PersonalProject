package com.qkj.project.service.impl;

import com.qkj.project.service.MonitorService;
import org.springframework.stereotype.Service;
import oshi.SystemInfo;
import oshi.hardware.CentralProcessor;
import oshi.hardware.HardwareAbstractionLayer;

import java.text.DecimalFormat;

/**
 * @author KeJiang Qi
 * @date 2024/12/5 - 11:50
 * @description
 */
@Service
public class MonitorServiceImpl implements MonitorService {

    // OSHI 提供获取所有数据的入口
    SystemInfo oshi = new SystemInfo();
    // 初始化获取硬件信息
    HardwareAbstractionLayer hardware = oshi.getHardware();
    // 初始化获取CPU信息
    CentralProcessor processor = hardware.getProcessor();

    /**
     * 获取CPU负载数据
     * @return 返回一个double数组，表示最近1秒内的CPU负载情况。数组长度通常等于CPU的逻辑核心数，
     *         每个元素的值介于0.0到1.0之间（包含0.0和1.0），表示该核心在最近1秒内的负载情况。
     */
    @Override
    public double[] getCpuLoad() {
        final int cpuLoadTick = 1000; // 设置CPU负载的采样间隔为1000毫秒（1秒）
        double[] processorCpuLoad = processor.getProcessorCpuLoad(cpuLoadTick);
        // 创建一个保留两位小数的格式化器
        DecimalFormat df = new DecimalFormat("#.000");

        // 遍历数组并格式化每个元素
        for (int i = 0; i < processorCpuLoad.length; i++) {
            processorCpuLoad[i] = Double.parseDouble(df.format(processorCpuLoad[i]));
        }
        return processorCpuLoad;
    }
}
