package com.qkj.project.vo;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import java.util.List;

/**
 * @author KeJiang Qi
 * @date 2024/8/20 - 17:51
 * @description 角色分配菜单实体类
 */
@Data
public class RoleMenuGrantVO {
    /**
     * 角色id
     */
    @NotBlank(message = "角色不能为空")
    private String roleId;
    private List<String> ids;
}
