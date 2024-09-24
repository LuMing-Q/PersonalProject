package com.qkj.project.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * @author KeJiang Qi
 * @date 2024/8/22 - 17:47
 * @description 菜单实体类
 */
@Data
public class Menu {
    private String id;
    @JsonProperty("parent_id")
    @NotBlank(message = "父級菜单不能为空")
    private String parentId;
    private int level;
    @NotBlank(message = "菜单名称不能为空")
    private String name;
    @NotBlank(message = "菜单页面路径不能为空")
    private String path;
    @NotNull(message = "排序不能为空")
    private int sort;
    private int available;
    private int type;
    @JsonProperty("op_directive")
    private String opDirective;
    private String icon;
}
