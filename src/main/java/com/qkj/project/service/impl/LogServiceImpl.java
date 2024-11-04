package com.qkj.project.service.impl;

import com.qkj.project.common.Page;
import com.qkj.project.dao.OptionLogDao;
import com.qkj.project.entity.OptionLog;
import com.qkj.project.service.OptionLogService;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

/**
 * @author KeJiang Qi
 * @date 2024/8/20 - 15:18
 * @description
 */
public class OptionLogServiceImpl implements OptionLogService {

    @Resource
    private OptionLogDao optionLogDao;

    @Override
    public Page<OptionLog> logsPage(int status, String operate, int page, int size) {
        long total = optionLogDao.selectCount(status, operate);
        if (total <= 0) { return Page.of(total, page, new ArrayList<>()); }
        List<OptionLog> logs = optionLogDao.selectLimit(status, operate, (page - 1) * size, size);
        return Page.of(total, page, logs);
    }

    @Override
    public OptionLog log(String id) {
        return optionLogDao.selectById(id);
    }

    @Override
    public List<String> operateOption() {
        return optionLogDao.operateOption();
    }
}
