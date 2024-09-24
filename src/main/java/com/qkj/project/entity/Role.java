package com.qkj.project.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import java.time.LocalDateTime;
import java.util.List;

/**
 * @author KeJiang Qi
 * @date 2024/8/22 - 17:44
 * @description 角色实体类
 */
@Data
public class Role {
    private String id;
    @NotBlank(message = "角色名称不能为空")
    private String name;
    private String code;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createTime;
    private List<RoleMenu> menus;
}
