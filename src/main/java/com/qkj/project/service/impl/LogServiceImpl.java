package com.qkj.project.service.impl;

import com.qkj.project.common.Page;
import com.qkj.project.dao.OptionLogDao;
import com.qkj.project.entity.OptionLog;
import com.qkj.project.service.LogService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author KeJiang Qi
 * @date 2024/8/20 - 15:18
 * @description 日志服务实现类
 */
@Service
public class LogServiceImpl implements LogService {

    @Resource
    private OptionLogDao optionLogDao;

    @Override
    public Page<OptionLog> logPage(Integer status, String operate, int page, int size) {
        long total = optionLogDao.selectCount(status, operate);
        if (total <= 0) { return Page.of(total, page, new ArrayList<>()); }
        List<OptionLog> logs = optionLogDao.selectLimit(status, operate, (page - 1) * size, size);
        return Page.of(total, page, logs);
    }

    @Override
    public OptionLog logDetail(String id) {
        return optionLogDao.selectById(id);
    }

    @Override
    public List<String> operateOption() {
        return optionLogDao.operateOption();
    }

    @Override
    public List<String> operateYearOption() {
        List<String> years = optionLogDao.operateYearOption();
        return years.stream().map(s -> s.substring(s.length() - 4)).
                sorted(Comparator.reverseOrder()).collect(Collectors.toList());
    }
}
