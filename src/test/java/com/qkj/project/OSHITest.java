package com.qkj.project;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import oshi.SystemInfo;
import oshi.hardware.CentralProcessor;
import oshi.hardware.GlobalMemory;
import oshi.hardware.HardwareAbstractionLayer;
import oshi.software.os.OSProcess;
import oshi.software.os.OperatingSystem;

import java.util.Arrays;
import java.util.List;

/**
 * @author KeJiang Qi
 * @date 2024/12/4 - 11:23
 * @description
 */
@Slf4j
public class OSHITest {
    // 创建 OSHI 对象
    SystemInfo oshi = new SystemInfo();

    /**
     * 获取 进程 相关测试
     */
    @Test
    public void processTest() {
        // 获取 os（操作系统） 对象
        OperatingSystem os = oshi.getOperatingSystem();
        // 获取所有进程信息
        List<OSProcess> processes = os.getProcesses();
        System.out.println("进程总数 = " + os.getProcessCount());
        ObjectMapper mapper = new ObjectMapper();
        processes.forEach(p -> {
            try {
                String json = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(p);
                System.out.println("进程信息 = " + json);
                System.out.println("==========================================");
            } catch (JsonProcessingException e) {
                throw new RuntimeException(e);
            }
        });
    }

    /**
     * 获取 内存相关测试
     */
    @SneakyThrows
    @Test
    public void memoryTest() {
        // 获取硬件信息对象
        HardwareAbstractionLayer hardware = oshi.getHardware();
        // 获取内存信息
        GlobalMemory memory = hardware.getMemory();
        ObjectMapper mapper = new ObjectMapper();

        String json = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(memory);
        System.out.println("内存信息 = " + json);

        for (int i = 0; i < 5; i++) {
            Thread.sleep(1000);
            System.out.println("可用内存 = " + memory.getAvailable()); // 单位是字节
        }
    }

    /**
     * 测试 CPU 相关测试
     */
    @Test
    public void cpuTest() {
        // 获取硬件信息对象
        HardwareAbstractionLayer hardware = oshi.getHardware();
        // CentralProcessorUnit（中央处理器单元） 是 CPU 的一个抽象概念，它包含了 CPU 的一个或多个物理核心（物理处理器）
        CentralProcessor cpu = hardware.getProcessor();
        // 获取 CPU 相关信息
        System.out.println("CPU 数量 = " + cpu.getPhysicalProcessorCount());
        System.out.println("CPU 逻辑数量 = " + cpu.getLogicalProcessorCount());
        // 获取 CPU 最大频率
        long maxFreq = cpu.getMaxFreq();
        System.out.println("CPU 最大频率 = " + maxFreq);
        // 获取 CPU 当前频率 -----> 返回的是逻辑cpu的频率
        long[] currentFreq = cpu.getCurrentFreq();
        System.out.println("CPU 当前频率 = " + Arrays.toString(currentFreq));
        // 获取 CPU 1s 内的 CPU 使用情况（逻辑CPU）
        double[] processorCpuLoad = cpu.getProcessorCpuLoad(1000);
        System.out.println("CPU 负载 = " + Arrays.toString(processorCpuLoad));
        CentralProcessor.ProcessorIdentifier processorIdentifier = cpu.getProcessorIdentifier();
        System.out.println("CPU 信息 = " + processorIdentifier);
    }
}
