package com.qkj.project.service;

import com.qkj.project.common.Page;
import com.qkj.project.entity.Role;

import java.util.List;

/**
 * @author KeJiang Qi
 * @date 2024/8/27 - 11:01
 * @description
 */
public interface RoleService {

    /**
     * 角色分页查询
     * @param name
     * @param page
     * @param size
     * @return
     */
    Page<Role> getRolePage(String name, int page, int size);
}
