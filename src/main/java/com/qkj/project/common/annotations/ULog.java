package com.qkj.project.common.annotations;

import java.lang.annotation.*;

/**
 * @author KeJiang Qi
 * @date 2024/8/20 - 14:57
 * @description 日志生成注解类
 */
@Inherited
@Documented
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface ULog {
    /**
     * 操作名称
     */
    String value();
}
