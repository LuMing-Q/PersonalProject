package com.qkj.project.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import java.time.LocalDateTime;

/**
 * @author KeJiang Qi
 * @date 2024/8/20 - 15:27
 * @description
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
public class User {
    private String id;
    @NotBlank(message = "用户名不能为空")
    private String username;
    @NotBlank(message = "真实姓名不能为空")
    private String realName;
    @NotBlank(message = "密码不能为空")
    private String password;
    @NotBlank(message = "角色不能为空")
    private String roleId;
    @Email
    private String email;
    private String phone;
    private String description;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createTime;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updateTime;
    private boolean status;

    public User(String id, String username, String realName, String email, String phone, boolean status) {
        this.id = id;
        this.username = username;
        this.password = "";
        this.roleId = "";
        this.realName = realName;
        this.email = email;
        this.phone = phone;
        this.description = "";
        this.status = status;
    }

    public void clear() {
        this.setPassword(null);
    }
}
