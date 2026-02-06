package com.qkj.project.entity;

import lombok.Data;
import lombok.experimental.Accessors;

/**
 * @author KeJiang Qi
 * @date 2024/8/22 - 17:45
 * @description 角色菜单关联实体类
 */
@Data
@Accessors(chain = true)
public class RoleMenu {
    private String id;
    private String roleId;
    private String menuId;
}
