package com.qkj.project.service.impl;

import com.qkj.project.common.Page;
import com.qkj.project.dao.MenuDao;
import com.qkj.project.entity.Menu;
import com.qkj.project.entity.RoleMenu;
import com.qkj.project.service.MenuService;
import com.qkj.project.utils.BaseUtil;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

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
    public int addRoleMenu(List<RoleMenu> list) {
        return menuDao.addRoleMenu(list);
    }

    /**
     * <img src="https://pic.imgdb.cn/item/66e52acad9c307b7e9ada728.webp">
     * @param name
     * @param page
     * @param size
     * @return
     */
    @Override
    public Page<Menu> getList(String name, int page, int size) {
        int total = menuDao.getCount(name);
        List<Menu> list = menuDao.getList(name, (page - 1) * size, size);
        return Page.of(total, page, list);
    }

    /**
     * <img src="https://pic.imgdb.cn/item/66e532e4d9c307b7e9b46b52.jpg" alt="8d362d1d75ff78c872e71e22e00ee4dc.jpg">
     * @param menuId
     * @return
     */
    @Override
    public int deleteMenuByMenuId(String menuId) {
        return menuDao.deleteMenuByMenuId(menuId);
    }

    @Override
    public Integer deleteRoleMenuByRoleId(String roleId) {
        return menuDao.deleteRoleMenuByRoleId(roleId);
    }
}
