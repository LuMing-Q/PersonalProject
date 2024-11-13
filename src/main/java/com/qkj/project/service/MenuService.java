package com.qkj.project.service;

import com.qkj.project.common.Page;
import com.qkj.project.entity.Menu;
import com.qkj.project.vo.RoleMenuGrantVO;

import java.util.List;

/**
 * @author KeJiang Qi
 * @date 2024/8/26 - 10:43
 * @description
 */
public interface MenuService {
    /**
     * 根据角色id查询所有菜单列表
     * @param roleId
     * @return
     */
    List<Menu> getMenuListByRoleId(String roleId);

    /**
     * 添加菜单
     * @param menu
     * @return
     */
    int addMenu(Menu menu);

    /**
     * 编辑菜单
     * @param menu
     * @return
     */
    int editMenu(Menu menu);

    /**
     * 角色菜单分配
     * @param menuGrant
     * @return
     */
    int addRoleMenu(RoleMenuGrantVO menuGrant);

    /**
     * 查询菜单分页列表
     * @param name
     * @param page
     * @param size
     * @return
     */
    Page<Menu> getList(String name, int page, int size);

    /**
     * 根据菜单id删除菜单
     * @param menuId
     * @return
     */
    int deleteMenuByMenuId(String menuId);
}
