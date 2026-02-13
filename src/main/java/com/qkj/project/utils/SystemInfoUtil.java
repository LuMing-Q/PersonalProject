package com.qkj.project.utils;

import cn.hutool.core.date.DatePattern;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.io.unit.DataUnit;
import cn.hutool.core.util.StrUtil;
import cn.hutool.system.oshi.OshiUtil;
import com.qkj.project.entity.oshi.DisksInfo;
import com.qkj.project.entity.oshi.NetworkInfo;
import com.qkj.project.entity.oshi.OSInfo;
import com.qkj.project.entity.oshi.OSRuntimeInfo;
import lombok.extern.slf4j.Slf4j;
import oshi.SystemInfo;
import oshi.hardware.CentralProcessor;
import oshi.hardware.GlobalMemory;
import oshi.hardware.HardwareAbstractionLayer;
import oshi.hardware.NetworkIF;
import oshi.software.os.FileSystem;
import oshi.software.os.OSFileStore;
import oshi.software.os.OperatingSystem;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.LineNumberReader;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.nio.charset.Charset;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

/**
 * @author KeJiang Qi
 * @date 2024/12/16 - 11:09
 * @description 系统信息获取工具类 <br>
 * 1.操作系统信息<br>
 * 2.系统cpu使用信息<br>
 * 3.系统内存信息<br>
 * 4.系统卡流量信息<br>
 * 5.磁盘使用量信息<br>
 */
@Slf4j
public class SystemInfoUtil {

    private static SystemInfo systemInfo;

    private static HardwareAbstractionLayer abstractionLayer;

    private static OperatingSystem operatingSystem;

    private static CentralProcessor centralProcessor;

    private static GlobalMemory globalMemory;

    private static Map<String, Long[]> networkInfoMap;

    private static List<NetworkIF> networkList;

    private static DecimalFormat df = new DecimalFormat("0.00");

    private SystemInfoUtil() {
    }

    /**
     * 静态代码块，初始化系统信息工具类<br>
     */
    static {
        try {
            systemInfo = new SystemInfo();
            abstractionLayer = systemInfo.getHardware();
            operatingSystem = systemInfo.getOperatingSystem();
            centralProcessor = abstractionLayer.getProcessor();
            globalMemory = abstractionLayer.getMemory();
            networkInfoMap = new ConcurrentHashMap<>();
            networkList = getNetwork();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 获取磁盘列表信息
     * @return
     */
    public static List<DisksInfo> getDisksList() {
        FileSystem fileSystem = operatingSystem.getFileSystem();
        List<OSFileStore> fileStores = fileSystem.getFileStores();
        List<DisksInfo> list = new ArrayList<>();
        for (int i = 0; i < fileStores.size(); i++) {
            OSFileStore osFileStore = fileStores.get(i);
            DisksInfo disksInfo = new DisksInfo();
            disksInfo.setDirName(osFileStore.getMount());
            String name = osFileStore.getName();
            disksInfo.setSysTypeName(name);
            disksInfo.setTypeName(osFileStore.getType());
            long total = osFileStore.getTotalSpace();
            long free = osFileStore.getUsableSpace();
            long used = total - free;
            if (used < 0) {
                // 有的挂载盘获取到的 free比total还大 不知是否是方法问题
                continue;
            }
            disksInfo.setTotal(total);
            disksInfo.setFree(free);
            disksInfo.setUsed(used);
            if (total != 0) {
                disksInfo.setUsage(Double.parseDouble(df.format((double) used / total)));
            }
            list.add(disksInfo);
        }
        return list;
    }

    /**
     * 获取网络接口NetworkIF对象列表
     * @return 网络接口NetworkIF对象列表
     */
    private static List<NetworkIF> getNetwork() {
        List<NetworkIF> list = new ArrayList<>();
        List<NetworkIF> networkIFs = abstractionLayer.getNetworkIFs();
        for (int i = 0; i < networkIFs.size(); i++) {
            NetworkIF networkIF = networkIFs.get(i);
            if (!networkIF.isKnownVmMacAddr()) {
                if (networkIF.getMacaddr() != null && networkIF.getIPv4addr().length > 0
                        && networkIF.getIPv6addr().length > 0) {
                    if (!networkInfoMap.containsKey(networkIF.getMacaddr())) {
                        networkIF.updateAttributes();
                        Long[] data = new Long[]{networkIF.getTimeStamp(), networkIF.getBytesSent(), networkIF.getBytesRecv()};
                        networkInfoMap.put(networkIF.getMacaddr(), data);
                    }
                    list.add(networkIF);
                }
            }
        }
        return list;
    }

    /**
     * 网卡信息枚举
     */
    enum NetworkType {
        /**
         * 当前网卡时间戳
         */
        TIME_STAMP(0),
        /**
         * 网卡总发送量
         */
        SEND(1),
        /**
         * 网卡总接收量
         */
        ACCEPT(2);
        private int index;

        NetworkType(int value) {
            this.index = value;
        }

        public int getIndex() {
            return index;
        }
    }

    /**
     * 网卡实际的传输速率受多方面影响，具体如下：<br>
     * 1、硬盘的读写（I/O）速度达不到。<br>
     * 2、网卡本身性能差。<br>
     * 3、交换机/（HUB）性能差。<br>
     * 4、通讯线路条件质量差。<br>
     * 5、网络性能差。<br>
     * 一般情况下，网络条件比较好的网络利用率100Mbits的一般为 1/8 (此方法默认)<br>
     * 网络及各方面条件差的利用率一般为 1/12
     */
    public static List<NetworkInfo> getNetworkInfo() {
        networkList = getNetwork();
        List<NetworkInfo> list = new ArrayList<>();
        for (int i = 0; i < networkList.size(); i++) {
            NetworkIF networkIF = networkList.get(i);
            if (networkIF.updateAttributes()) {
                if (networkIF.getIPv4addr().length > 0 && networkIF.getIPv6addr().length > 0) {
                    NetworkInfo networkInfo = new NetworkInfo();
                    networkInfo.setIpv4Address(networkIF.getIPv4addr()[0]);
                    networkInfo.setIpv6Address(networkIF.getIPv6addr()[0]);
                    networkInfo.setMacAddress(networkIF.getMacaddr());
                    networkInfo.setNetworkName(networkIF.getName());

                    //计算
                    Long[] oldData = networkInfoMap.get(networkIF.getMacaddr());
                    long time = oldData[NetworkType.TIME_STAMP.getIndex()] - networkIF.getTimeStamp();
                    if (time == 0) {
                        continue;
                    }
                    long send = (oldData[NetworkType.SEND.getIndex()] - networkIF.getBytesSent()) * 8 / time * 1000;
                    long accept = (oldData[NetworkType.ACCEPT.getIndex()] - networkIF.getBytesRecv()) * 8 / time * 1000;
                    Long[] newData = new Long[]{networkIF.getTimeStamp(), networkIF.getBytesSent(), networkIF.getBytesRecv()};
                    networkInfoMap.put(networkInfo.getMacAddress(), newData);

                    //对象赋值
                    networkInfo.setTimeStamp(networkIF.getTimeStamp());
                    networkInfo.setSend(send);
                    networkInfo.setAccept(accept);
                    list.add(networkInfo);
                }
            }
        }
        return list;
    }

    /**
     * 获取系统运行信息
     * @return 系统运行信息
     */
    public static OSRuntimeInfo getOSRuntimeInfo() {
        OSRuntimeInfo osRuntimeInfo = new OSRuntimeInfo();
        osRuntimeInfo.setTimestamp(DateUtil.now());
        //cpu使用率
        osRuntimeInfo.setCpuUsage(getCpuRate());
        //cpu基准速度（GHz）
        osRuntimeInfo.setCpuMaxFreq(df.format(centralProcessor.getMaxFreq() / 1000000000.0) + " GHz");
        //cpu当前速度（GHz）
        long[] currentFreq = centralProcessor.getCurrentFreq();
        long avg = Arrays.stream(currentFreq).sum() / currentFreq.length;
        osRuntimeInfo.setCpuCurrentFreq(df.format(avg / 1000000000.0) + " GHz");
        //系统内存总量
        osRuntimeInfo.setTotalMemory(globalMemory.getTotal());
        //系统使用量
        osRuntimeInfo.setUsedMemory(globalMemory.getTotal() - globalMemory.getAvailable());
        //可用虚拟总内存
        osRuntimeInfo.setSwapTotalMemory(globalMemory.getVirtualMemory().getSwapTotal());
        //已用虚拟内存
        osRuntimeInfo.setSwapUsedMemory(globalMemory.getVirtualMemory().getSwapUsed());
        //磁盘信息
        osRuntimeInfo.setDisksList(getDisksList());
        //磁盘读取速率
        Map<String, Double> diskIo = getDiskIo();
        double diskReadRate = diskIo.get("diskReadRate");
        osRuntimeInfo.setDiskReadRate(diskReadRate);
        //磁盘写入速率
        double diskWriteRate = diskIo.get("diskWriteRate");
        osRuntimeInfo.setDiskWriteRate(diskWriteRate);
        //网卡信息
        osRuntimeInfo.setNetworkList(getNetworkInfo());
        return osRuntimeInfo;
    }

    /**
     * 系统信息
     * @return
     */
    public static OSInfo getSystemInfo() {
        Properties props = System.getProperties();
        OSInfo osInfo = new OSInfo();
        //操作系统
        osInfo.setOs(props.getProperty("os.name"));
        //系统架构
        osInfo.setOsArch(props.getProperty("os.arch"));
        //java版本
        osInfo.setJavaVersion(props.getProperty("java.version"));
        //工作目录
        osInfo.setUserDir(props.getProperty("user.dir"));
        //CPU核数
        osInfo.setCpuCount(centralProcessor.getLogicalProcessorCount());
        //主机信息
        try {
            InetAddress address = InetAddress.getLocalHost();
            osInfo.setHost(address.getHostAddress());
            osInfo.setHostName(address.getHostName());
        } catch (UnknownHostException e) {
            log.error("主机信息获取失败");
        }
        //系统启动时间
        osInfo.setBootTime(getBootTime());
        return osInfo;
    }

    /**
     * @return cpu使用率
     * @throws InterruptedException
     */
    public static double getCpuRate() {
        CentralProcessor processor = OshiUtil.getHardware().getProcessor();
        long[] prevTicks = processor.getSystemCpuLoadTicks();
        try {
            // 等待5秒，获取新的CPU负载
            TimeUnit.SECONDS.sleep(5);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        long[] ticks = processor.getSystemCpuLoadTicks();
        long userDiff = calculateDiff(ticks, prevTicks, CentralProcessor.TickType.USER);
        long niceDiff = calculateDiff(ticks, prevTicks, CentralProcessor.TickType.NICE);
        long systemDiff = calculateDiff(ticks, prevTicks, CentralProcessor.TickType.SYSTEM);
        long idleDiff = calculateDiff(ticks, prevTicks, CentralProcessor.TickType.IDLE);
        long iowaitDiff = calculateDiff(ticks, prevTicks, CentralProcessor.TickType.IOWAIT);
        long irqDiff = calculateDiff(ticks, prevTicks, CentralProcessor.TickType.IRQ);
        long softirqDiff = calculateDiff(ticks, prevTicks, CentralProcessor.TickType.SOFTIRQ);
        long stealDiff = calculateDiff(ticks, prevTicks, CentralProcessor.TickType.STEAL);
        long totalCpu = userDiff + niceDiff + systemDiff + idleDiff + iowaitDiff + irqDiff + softirqDiff + stealDiff;
        double cpuUsage = 1.0 - (idleDiff * 1.0 / totalCpu);
        return cpuUsage;
    }

    /**
     * 计算两个tick数组之间的差异
     * @param ticks 当前tick数组
     * @param prevTicks 前一个tick数组
     * @param type tick类型
     * @return
     */
    private static long calculateDiff(long[] ticks, long[] prevTicks, CentralProcessor.TickType type) {
        return ticks[type.ordinal()] - prevTicks[type.ordinal()];
    }

    /**
     * 获取系统启动时间
     *
     * @return
     */
    public static String getBootTime() {
        String uptime = "";
        String os = System.getProperty("os.name").toLowerCase();
        try {
            if (os.contains("win")) {
                Process uptimeProc = Runtime.getRuntime().exec("net statistics Workstation");
                BufferedReader in = new BufferedReader(new InputStreamReader(uptimeProc.getInputStream(), Charset.forName("GBK")));
                String line;
                while ((line = in.readLine()) != null) {
                    if (line.startsWith("Statistics since")) {
                        SimpleDateFormat format = new SimpleDateFormat("'Statistics since' yyyy/MM/dd HH:mm:ss");
                        Date bootTime = format.parse(line);
                        uptime = DateUtil.format(bootTime, DatePattern.NORM_DATETIME_PATTERN);
                        break;
                    } else if (line.startsWith("统计数据开始于")) {
                        SimpleDateFormat format = new SimpleDateFormat("'统计数据开始于' yyyy/MM/dd HH:mm:ss");
                        Date bootTime = format.parse(line);
                        uptime = DateUtil.format(bootTime, DatePattern.NORM_DATETIME_PATTERN);
                        break;
                    }
                }
            } else if (os.contains("mac") || os.contains("nix") || os.contains("nux") || os.contains("aix")) {
                Process uptimeProc = Runtime.getRuntime().exec("uptime -s");
                BufferedReader in = new BufferedReader(new InputStreamReader(uptimeProc.getInputStream()));
                String line = in.readLine();
                if (line != null) {
                    uptime = line;
                }
            }
        } catch (Exception e) {
            log.error("获取系统启动时间异常: {}", e.getMessage());
        }
        return uptime;
    }

    /**
     * 获取磁盘读写速度（Kb/秒）
     *
     * @return
     */
    public static Map<String, Double> getDiskIo() {
        Map<String, Double> map = new HashMap<>();
        StringBuilder sb = new StringBuilder();
        String os = System.getProperty("os.name").toLowerCase();
        try {
            if (os.contains("mac") || os.contains("nix") || os.contains("nux") || os.contains("aix")) {
                Process pos = Runtime.getRuntime().exec("iostat -d 1 2");
                pos.waitFor();
                InputStreamReader isr = new InputStreamReader(pos.getInputStream());
                LineNumberReader lnr = new LineNumberReader(isr);
                String line;
                while ((line = lnr.readLine()) != null) {
                    sb.append(line).append("\n");
                }
            }
            String info = sb.toString();
            if (StrUtil.isEmpty(info)) {
                map.put("diskReadRate", 0D);
                map.put("diskWriteRate", 0D);
                return map;
            }
            String[] data = info.split("\n");
            for (int i = 7; i < data.length; i++) {
                String[] numdata = data[i].split(" +");
                String devName = numdata[0];
                double diskReadRate = Double.parseDouble(numdata[2]); //磁盘读数据速率
                double diskWriteRate = Double.parseDouble(numdata[3]); //磁盘写数据速率
                //这里简单统计，只需要统计一个就行，直接break结束循环
                map.put("diskReadRate", diskReadRate);
                map.put("diskWriteRate", diskWriteRate);
                break;
            }
        } catch (Exception e) {
            map.put("diskReadRate", 0D);
            map.put("diskWriteRate", 0D);
            log.error("获取磁盘传输速度异常: {}", e.getMessage());
        }
        return map;
    }

    /**
     * 格式化输出百分比工具类
     *
     * @param rate 待格式化数据
     *
     * <pre>
     *     0.1234   -> 12.34%
     *     1.2      -> 120%
     * </pre>
     * @return
     */
    public static String formatRate(double rate) {
        return new DecimalFormat("#.##%").format(rate);
    }

    /**
     * 格式化输出大小 B/KB/MB/GB/TB/PB/EB 单位转换工具类
     * @param size 字节大小
     * @return
     */
    public static String formatData(long size) {
        if (size <= 0L) {
            return "0B";
        } else {
            int digitGroups = Math.min(DataUnit.UNIT_NAMES.length - 1, (int) (Math.log10((double) size) / Math.log10(1024.0D)));
            return (new DecimalFormat("#,##0.##")).format((double) size / Math.pow(1024.0D, (double) digitGroups)) + " " + DataUnit.UNIT_NAMES[digitGroups];
        }
    }
}


