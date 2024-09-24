package com.qkj.project.service;

import com.qkj.project.common.Page;
import com.qkj.project.entity.Menu;
import com.qkj.project.entity.RoleMenu;

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
     * 角色授权
     * @param list
     * @return
     */
    int addRoleMenu(List<RoleMenu> list);

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

    /**
     * 根据角色id删除已授权菜单
     * @param roleId
     * @return
     */
    Integer deleteRoleMenuByRoleId(String roleId);

}
