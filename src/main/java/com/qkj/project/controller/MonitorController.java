package com.qkj.project.controller;

import com.qkj.project.service.MonitorService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

/**
 * @author KeJiang Qi
 * @date 2024/12/4 - 11:07
 * @description 监控面板控制类
 *  使用 OSHI 查询操作系统和硬件信息
 */
@RequestMapping("/monitor")
@RestController
public class MonitorController {

    @Resource
    private MonitorService monitorService;


    @GetMapping("/cpu_load")
    public double[] getCpuLoad() {
        return monitorService.getCpuLoad();
    }
    
}
