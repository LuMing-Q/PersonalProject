package com.qkj.project;

import cn.hutool.core.date.DatePattern;
import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.StrUtil;
import com.qkj.project.entity.oshi.DisksInfo;
import com.qkj.project.entity.oshi.NetworkInfo;
import com.qkj.project.entity.oshi.OSInfo;
import com.qkj.project.entity.oshi.OSRuntimeInfo;
import com.qkj.project.utils.SystemInfoUtil;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

import java.util.List;

/**
 * @author KeJiang Qi
 * @date 2024/12/16 - 11:09
 * @description 系统信息获取测试类
 */
@Slf4j
public class SystemInfoTest {
    @Test
    public void test() {
        //系统信息
        System.out.println("-----------系统信息-----------");
        OSInfo osInfo = SystemInfoUtil.getSystemInfo();
        System.out.println("操作系统："+ osInfo.getOs());
        System.out.println("系统架构："+ osInfo.getOsArch());
        System.out.println("Java版本："+ osInfo.getJavaVersion());
        System.out.println("工作目录："+ osInfo.getUserDir());
        System.out.println("cpu核心数："+ osInfo.getCpuCount());
        System.out.println("主机host："+ osInfo.getHost());
        System.out.println("主机名称："+ osInfo.getHostName());
        String bootTime = osInfo.getBootTime();
        String runTime = "";
        if (StrUtil.isNotEmpty(bootTime)) {
            DateTime start = DateUtil.parse(bootTime, DatePattern.NORM_DATETIME_PATTERN);
            runTime = DateUtil.formatBetween(start, DateUtil.date());
        }
        System.out.println("系统正常运行时长："+ runTime);
        //运行时信息
        OSRuntimeInfo osRuntimeInfo = SystemInfoUtil.getOSRuntimeInfo();
        //1.CPU信息
        System.out.println("------cpu信息------");
        System.out.println("cpu使用率：" + SystemInfoUtil.formatRate(osRuntimeInfo.getCpuUsage()));
        System.out.println("cpu基准速度：" + osRuntimeInfo.getCpuMaxFreq());
        System.out.println("cpu速度：" + osRuntimeInfo.getCpuCurrentFreq());
        //2.内存信息
        System.out.println("------内存信息------");
        //系统内存总量
        long total = osRuntimeInfo.getTotalMemory();
        long used = osRuntimeInfo.getUsedMemory();
        double usage = used * 1.0 / total;
        System.out.println("系统内存总量：" + total + " -> " + SystemInfoUtil.formatData(total));
        System.out.println("系统内存使用量：" + used + " -> " + SystemInfoUtil.formatData(used));
        System.out.println("系统内存使用率：" + SystemInfoUtil.formatRate(usage));
        //可用虚拟总内存
        long swapTotal = osRuntimeInfo.getSwapTotalMemory();
        //已用虚拟内存
        long swapUsed = osRuntimeInfo.getSwapUsedMemory();
        System.out.println("可用虚拟总内存(swap)：" + swapTotal + " -> " + SystemInfoUtil.formatData(swapTotal));
        System.out.println("虚拟内存使用量(swap)：" + swapUsed + " -> " + SystemInfoUtil.formatData(swapUsed));
        //3.磁盘信息
        System.out.println("------磁盘信息------");
        System.out.println("磁盘读取速度：" + osRuntimeInfo.getDiskReadRate() + "Kb/s");
        System.out.println("磁盘写入速度：" + osRuntimeInfo.getDiskWriteRate() + "Kb/s");
        List<DisksInfo> disksList = osRuntimeInfo.getDisksList();
        for (DisksInfo disksInfo : disksList) {
            System.out.println("挂载点：" + disksInfo.getDirName());
            System.out.println("文件系统名称：" + disksInfo.getSysTypeName());
            System.out.println("文件系统类型：" + disksInfo.getTypeName());
            System.out.println("磁盘总量：" + disksInfo.getTotal() + " -> " + SystemInfoUtil.formatData(disksInfo.getTotal()));
            System.out.println("磁盘使用量：" + disksInfo.getUsed() + " -> " + SystemInfoUtil.formatData(disksInfo.getUsed()));
            System.out.println("磁盘剩余量：" + disksInfo.getFree() + " -> " + SystemInfoUtil.formatData(disksInfo.getFree()));
            System.out.println("磁盘使用率：" + SystemInfoUtil.formatRate(disksInfo.getUsage()));
        }
        //4.网卡网络信息
        List<NetworkInfo> netList = SystemInfoUtil.getNetworkInfo();
        System.out.println("------网卡网络信息------");
        for (NetworkInfo networkInfo : netList) {
            System.out.println("ipv4地址："+networkInfo.getIpv4Address());
            System.out.println("mac地址："+networkInfo.getMacAddress());
            System.out.println("网卡名称："+networkInfo.getNetworkName());
            double send = networkInfo.getSend() / 1024.0;
            double accept = networkInfo.getAccept() / 1024.0;
            System.out.println("上传速度↑："+String.format("%.1f%s", send, "Kbps"));
            System.out.println("下载速度↓："+String.format("%.1f%s", accept, "Kbps"));
        }
    }
}


