package com.qkj.project.task;

import com.qkj.project.dao.OptionLogDao;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.time.LocalDateTime;

/**
 * @author KeJiang Qi
 * @date 2026/2/10 - 17:27
 * @description 启动时创建操作日志的对应年份表
 */
@Slf4j
@Component
public class StartCreateOptionLogTableTask implements CommandLineRunner {
    @Resource
    private OptionLogDao optionLogDao;

    @Override
    public void run(String... args) {
        LocalDateTime now = LocalDateTime.now();
        int year = now.getYear();
        log.info("Start create table t_option_log_{} ...", year);
        optionLogDao.createTable(year);
        log.info("End create table t_option_log_{} !", year);
    }
}
