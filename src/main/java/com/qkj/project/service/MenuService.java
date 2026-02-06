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
     * @param roleId 角色id
     * @return 菜单列表
     */
    List<Menu> getMenuListByRoleId(String roleId);

    /**
     * 添加菜单
     * @param menu 菜单
     * @return 新建结果
     */
    int addMenu(Menu menu);

    /**
     * 编辑菜单
     * @param menu 菜单
     * @return 编辑结果
     */
    int editMenu(Menu menu);

    /**
     * 角色菜单分配
     * @param menuGrant 角色菜单授权VO
     * @return 授权结果
     */
    int addRoleMenu(RoleMenuGrantVO menuGrant);

    /**
     * 根据 name 筛选菜单列表
     * @param name 菜单名称
     * @return 菜单列表
     */
    List<Menu> getListByName(String name);

    /**
     * 根据菜单id删除菜单
     * @param menuId 菜单id
     * @return 删除结果
     */
    int deleteMenuByMenuId(String menuId);

    /**
     * 查询所有菜单列表
     * @return 所有菜单
     */
    List<Menu> getAll();
}
