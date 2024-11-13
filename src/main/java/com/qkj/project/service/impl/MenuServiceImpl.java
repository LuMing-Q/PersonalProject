package com.qkj.project.service.impl;

import com.qkj.project.common.Page;
import com.qkj.project.dao.MenuDao;
import com.qkj.project.entity.Menu;
import com.qkj.project.entity.RoleMenu;
import com.qkj.project.service.MenuService;
import com.qkj.project.utils.BaseUtil;
import com.qkj.project.vo.RoleMenuGrantVO;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author KeJiang Qi
 * @date 2024/8/26 - 10:45
 * @description
 */
@Service
public class MenuServiceImpl implements MenuService {

    @Resource
    private MenuDao menuDao;

    @Override
    public List<Menu> getMenuListByRoleId(String roleId) {
        return menuDao.selectMenuByRoleId(roleId);
    }

    @Override
    public int addMenu(Menu menu) {
        menu.setId(BaseUtil.uuid());
        return menuDao.addMenu(menu);
    }

    @Override
    public int editMenu(Menu menu) {
        return menuDao.editMenu(menu);
    }

    @Override
    public int addRoleMenu(RoleMenuGrantVO menuGrant) {
        menuDao.deleteRoleMenuByRoleId(menuGrant.getRoleId());
        List<RoleMenu> list = menuGrant.getIds().stream().
                map(m -> new RoleMenu().setId(BaseUtil.uuid()).setRoleId(menuGrant.getRoleId()).setMenuId(m)).
                collect(Collectors.toList());
        return menuDao.addRoleMenu(list);
    }

    @Override
    public Page<Menu> getList(String name, int page, int size) {
        int total = menuDao.getCount(name);
        List<Menu> list = menuDao.getList(name, (page - 1) * size, size);
        return Page.of(total, page, list);
    }

    /**
     * @param menuId
     * @return
     */
    @Override
    public int deleteMenuByMenuId(String menuId) {
        return menuDao.deleteMenuByMenuId(menuId);
    }
}
