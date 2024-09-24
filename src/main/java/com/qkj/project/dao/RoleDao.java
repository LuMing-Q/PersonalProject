package com.qkj.project.dao;

import com.qkj.project.entity.Role;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @author KeJiang Qi
 * @date 2024/8/22 - 17:43
 * @description
 */
@Mapper
public interface RoleDao {

    /**
     * 角色新增
     * @param role
     * @return
     */
    Integer addRole(Role role);

    /**
     * 角色修改
     * @param role
     * @return
     */
    Integer updateRole(Role role);

    /**
     * 角色删除
     * @param roleId
     * @return
     */
    Integer deleteRole(@Param("roleId") String roleId);

    /**
     * 通过id查找
     * @param id
     * @return
     */
    Role selectRoleById(@Param("id") String id);

    /**
     * 通过名称查找的分页查询
     * @param page
     * @param size
     * @param name
     * @return
     */
    List<Role> getRoles(@Param("page") int page, @Param("size")  int size , @Param("name") String name);

    /**
     * 通过角色名称查找符合要求的总数
     * @param name
     * @return
     */
    Integer getRoleTotal(@Param("name") String name);
}
