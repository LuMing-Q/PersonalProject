package com.qkj.project.dao;

import com.qkj.project.entity.OptionLog;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @author KeJiang Qi
 * @date 2024/8/20 - 15:41
 * @description
 */
@Mapper
public interface OptionLogDao {
    /**
     * 新增/修改日志  --->  第一次新增没有返回结果和，所以使用 ON DUPLICATE 更新状态和操作结果
     * @param log
     * @return
     */
    int upsert(OptionLog log);

    /**
     * 查询符合条件的日志总数
     * @param status
     * @param operate
     * @return
     */
    long selectCount(@Param("status") int status, @Param("operate") String operate);

    /**
     * 分页查询
     * @param status
     * @param operate 操作类型
     * @param page
     * @param size
     * @return
     */
    List<OptionLog> selectLimit(@Param("status") int status, @Param("operate") String operate, @Param("page") int page, @Param("size") int size);

    /**
     * 详情查看
     * @param id
     * @return
     */
    OptionLog selectById(@Param("id") String id);

    /**
     * 操作类型查询
     * @return
     */
    List<String> operateOption();
}
