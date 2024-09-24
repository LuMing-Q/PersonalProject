package com.qkj.project.controller;

import com.qkj.project.common.Page;
import com.qkj.project.entity.Role;
import com.qkj.project.service.RoleService;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

/**
 * @author KeJiang Qi
 * @date 2024/8/27 - 10:59
 * @description
 */
@RestController
@RequestMapping("/roles")
public class RoleController {

    @Resource
    private RoleService roleService;

    @GetMapping
    public Page<Role> getRolePage(@RequestParam(value = "name", required = false) String name,
                                  @RequestParam(value = "page", required = false, defaultValue = "1") int page,
                                  @RequestParam(value = "size", required = false, defaultValue = "10") int size) {
        return roleService.getRolePage(name, page, size);
    }
}
