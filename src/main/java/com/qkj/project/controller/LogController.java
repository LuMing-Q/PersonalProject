package com.qkj.project.controller;

import com.qkj.project.common.Page;
import com.qkj.project.entity.OptionLog;
import com.qkj.project.service.LogService;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

/**
 * @author KeJiang Qi
 * @date 2024/8/20 - 15:14
 * @description 日志处理控制类
 */
@RestController
@RequestMapping("/log")
public class LogController {
    @Resource
    private LogService optionLogService;

    @GetMapping("/page")
    public Page<OptionLog> logPage(@RequestParam(value = "status", required = false) Integer status,
                                    @RequestParam(value = "operate", required = false) String operate,
                                    @RequestParam(value = "page", required = false, defaultValue = "1") Integer page,
                                    @RequestParam(value = "size", required = false, defaultValue = "10") Integer size) {
        return optionLogService.logPage(status, operate, page, size);
    }

    @GetMapping("/{log_id}")
    public OptionLog log(@PathVariable("log_id") String logId) {
        return optionLogService.logDetail(logId);
    }

    @GetMapping("/option")
    public List<String> operateOption() {
        return optionLogService.operateOption();
    }
}
