package com.qkj.project.task;

import com.qkj.project.dao.OptionLogDao;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.time.LocalDateTime;

/**
 * @author KeJiang Qi
 * @date 2026/2/12 - 10:13
 * @description 定时任务
 */
@Slf4j
@Component
public class TimedTask {
    @Resource
    private OptionLogDao optionLogDao;

    /**
     * 创建操作日志的对应年份表
     */
    @Scheduled(cron = "0 57 23 31 12 *")
    public void createOptionLogTable() {
        LocalDateTime now = LocalDateTime.now();
        int year = now.getYear() + 1;
        log.info("Start create table option_log_{} ...", year);
        optionLogDao.createTable(year);
        log.info("End create table option_log_{} !", year);
    }
}
