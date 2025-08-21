package com.qkj.project.utils;

import com.qkj.project.common.enumerations.StatusCode;
import com.qkj.project.common.exception.BusinessException;

/**
 * @author KeJiang Qi
 * @date 2025/2/21 - 15:40
 * @description 基于雪花算法的ID生成器
 */
public class SnowflakeId {
    // 起始的时间戳，2022-01-01 00:00:00
    private static final long twepoch = 1735660800000L;

    // 每个部分占用的位数
    private static final long workerIdBits = 5L; // 机器标识位数
    private static final long dataCenterIdBits = 5L; // 数据中心标识位数
    private static final long sequenceBits = 12L; // 序列号位数

    // 每个部分的最大值
    private static final long maxWorkerId = ~(-1L << workerIdBits);
    private static final long maxDataCenterId = ~(-1L << dataCenterIdBits);
    private static final long sequenceMask = ~(-1L << sequenceBits);

    // 每个部分向左的位移
    private static final long workerIdShift = sequenceBits;
    private static final long dataCenterIdShift = sequenceBits + workerIdBits;
    private static final long timestampShift = sequenceBits + workerIdBits + dataCenterIdBits;

    private final long workerId; // 机器ID
    private final long dataCenterId; // 数据中心ID
    private long sequence = 0L; // 序列号
    private long lastTimestamp = -1L; // 上次生成ID的时间戳

    public SnowflakeId(long workerId, long dataCenterId) {
        if (workerId > maxWorkerId || workerId < 0) {
            throw BusinessException.of(StatusCode.CODE_504, "Worker ID超出范围");
        }
        if (dataCenterId > maxDataCenterId || dataCenterId < 0) {
            throw BusinessException.of(StatusCode.CODE_504, "Data Center ID超出范围");
        }
        this.workerId = workerId;
        this.dataCenterId = dataCenterId;
    }

    public synchronized long generate() {
        long timestamp = System.currentTimeMillis();
        // 如果当前时间小于上次生成ID的时间戳，说明系统时钟回退过，抛出异常
        if (timestamp < lastTimestamp) {
            throw BusinessException.of(StatusCode.CODE_504, "系统时钟回退，无法生成ID");
        }
        // 如果是同一毫秒内生成的ID，自增序列号
        if (timestamp == lastTimestamp) {
            sequence = (sequence + 1) & sequenceMask;
            // 序列号溢出，等待下一毫秒
            if (sequence == 0) {
                timestamp = tilNextMillis(lastTimestamp);
            }
        } else {
            sequence = 0L;
        }
        lastTimestamp = timestamp;
        return ((timestamp - twepoch) << timestampShift)
                | (dataCenterId << dataCenterIdShift)
                | (workerId << workerIdShift)
                | sequence;
    }

    private long tilNextMillis(long lastTimestamp) {
        long timestamp = System.currentTimeMillis();
        while (timestamp <= lastTimestamp) {
            timestamp = System.currentTimeMillis();
        }
        return timestamp;
    }
}
