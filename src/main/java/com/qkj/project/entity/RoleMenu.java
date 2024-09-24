package com.qkj.project.entity;

import lombok.Data;

/**
 * @author KeJiang Qi
 * @date 2024/8/22 - 17:45
 * @description 角色菜单关联实体类
 */
@Data
public class RoleMenu {
    private String id;
    private String roleId;
    private int menuId;
}
