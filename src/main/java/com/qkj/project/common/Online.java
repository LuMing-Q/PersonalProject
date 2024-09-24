package com.qkj.project.common;

import com.qkj.project.entity.User;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author KeJiang Qi
 * @date 2024/8/20 - 14:57
 * @description 当前线程用户信息
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Online {
    private String token;
    private User user;
}
