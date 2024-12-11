package com.qkj.project.service.impl;

import com.qkj.project.common.Page;
import com.qkj.project.common.enumerations.StatusCode;
import com.qkj.project.common.exception.BusinessException;
import com.qkj.project.dao.RoleDao;
import com.qkj.project.entity.Role;
import com.qkj.project.service.RoleService;
import com.qkj.project.utils.BaseUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.time.LocalDateTime;
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

    @Override
    public int addRole(Role role) {
        role.setId(BaseUtil.uuid());
        role.setCreateTime(LocalDateTime.now());
        return roleDao.addRole(role);
    }

    @Override
    public int updateRole(Role role) {
        return roleDao.updateRole(role);
    }

    @Override
    public int deleteRole(String id) {
        Integer roleBuiltIn = roleDao.getRoleBuiltIn(id);
        if (roleBuiltIn == null) {
            throw BusinessException.of(StatusCode.CODE_400, "角色不存在");
        }
        if (roleBuiltIn == 1) {
            throw BusinessException.of(StatusCode.CODE_403, "内置角色不能删除");
        }
        return roleDao.deleteRole(id);
    }
}
