package com.qkj.project.service;

import com.qkj.project.common.Page;
import com.qkj.project.entity.OptionLog;

import java.util.List;

/**
 * @author KeJiang Qi
 * @date 2024/8/20 - 15:15
 * @description 日志业务处理类
 */
public interface LogService {
    /**
     * 分页列表查询
     * @param status
     * @param operate
     * @param page
     * @param size
     * @return
     */
    Page<OptionLog> logPage(Integer status, String operate, int page, int size);

    /**
     * 详情查看
     * @param id
     * @return
     */
    OptionLog logDetail(String id);

    /**
     * 操作类型查询
     * @return
     */
    List<String> operateOption();
}
