package com.qkj.project.service.impl;

import com.qkj.project.common.Page;
import com.qkj.project.dao.RoleDao;
import com.qkj.project.entity.Role;
import com.qkj.project.service.RoleService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
 * @author KeJiang Qi
 * @date 2024/8/27 - 11:02
 * @description
 */
@Slf4j
@Service
public class RoleServiceImpl implements RoleService {

    @Resource
    private RoleDao roleDao;

    @Override
    public Page<Role> getRolePage(String name, int page, int size) {
        int total = roleDao.getRoleTotal(name);
        List<Role> list = roleDao.getRoles(size * (page - 1), size, name);
        return Page.of(total, page, list);
    }
}
