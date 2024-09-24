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
     * @param list
     * @return
     */
    Integer addRoleMenu(@Param("list") List<RoleMenu> list);

    /**
     * 根据角色id删除已授权菜单
     * @param roleId
     * @return
     */
    Integer deleteRoleMenuByRoleId(@Param("roleId") String roleId);

    /**
     * 根据角色id查询菜单列表
     * @param roleId
     * @return
     */
    List<Menu> selectMenuByRoleId(@Param("roleId") String roleId);

    /**
     * 新建菜单
     * @param menu
     * @return
     */
    Integer addMenu(Menu menu);

    int getCount(@Param("name") String name);

    List<Menu> getList(@Param("name") String name, @Param("page") int page, @Param("size") int size);

    Integer deleteMenuByMenuId(@Param("menuId") String menuId);
}
