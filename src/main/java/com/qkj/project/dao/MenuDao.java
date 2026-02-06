package com.qkj.project.dao;

import com.qkj.project.entity.Menu;
import com.qkj.project.entity.RoleMenu;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @author KeJiang Qi
 * @date 2024/8/22 - 17:54
 * @description
 */
@Mapper
public interface MenuDao {

    /**
     * 角色菜单授权
     * @param list 角色菜单列表
     * @return 授权结果
     */
    Integer addRoleMenu(@Param("list") List<RoleMenu> list);

    /**
     * 根据角色id删除已授权菜单
     * @param roleId 角色id
     * @return 删除结果
     */
    Integer deleteRoleMenuByRoleId(@Param("roleId") String roleId);

    /**
     * 根据角色id查询菜单列表
     * @param roleId 角色id
     * @return 菜单列表
     */
    List<Menu> selectMenuByRoleId(@Param("roleId") String roleId);

    /**
     * 新建菜单
     * @param menu 菜单
     * @return 新建结果
     */
    Integer addMenu(Menu menu);

    /**
     * 根据 name 筛选菜单数量
     * @param name 菜单名称
     * @return 菜单数量
     */
    int getCount(@Param("name") String name);

    /**
     * 根据 name 筛选菜单列表
     * @param name 菜单名称
     * @return 菜单列表
     */
    List<Menu> getListByName(@Param("name") String name);

    /**
     * 根据 id 删除菜单
     * @param menuId 菜单id
     * @return 删除结果
     */
    Integer deleteMenuByMenuId(@Param("menuId") String menuId);

    /**
     * 编辑菜单
     * @param menu 菜单
     * @return 编辑结果
     */
    int editMenu(Menu menu);

    /**
     * 查询所有菜单
     * @return 所有菜单
     */
    List<Menu> getAll();
}
