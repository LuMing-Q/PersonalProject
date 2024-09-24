package com.qkj.project.common;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.List;

/**
 * @author KeJiang Qi
 * @date 2024/8/20 - 14:57
 * @description 分页
 */
@Setter
@Getter
@AllArgsConstructor
public class Page<T> implements Serializable {
    private long total;
    private int size;
    private int page;
    private List<T> data;

    public static <T> Page<T> of(long total, int page, List<T> data) {
        return new Page<>(total, data.size(), page, data);
    }
}
