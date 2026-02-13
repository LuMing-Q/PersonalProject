package com.qkj.project.dao;

import com.qkj.project.entity.OptionLog;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @author KeJiang Qi
 * @date 2024/8/20 - 15:41
 * @description 日志持久层接口
 */
@Mapper
public interface OptionLogDao {
    /**
     * 新增/修改日志  --->  第一次新增没有返回结果和，所以使用 ON DUPLICATE 更新状态和操作结果
     * @param log 日志对象
     * @return 影响行数
     */
    Integer upsert(OptionLog log);

    /**
     * 查询符合条件的日志总数
     * @param status 状态
     * @param operate 操作类型
     * @return 日志总数
     */
    Long selectCount(@Param("status") Integer status, @Param("operate") String operate);

    /**
     * 分页查询
     * @param status 状态
     * @param operate 操作类型
     * @param page 页码
     * @param size 每页数量
     * @return 日志列表
     */
    List<OptionLog> selectLimit(@Param("status") Integer status, @Param("operate") String operate, @Param("page") int page, @Param("size") int size);

    /**
     * 详情查看
     * @param id 日志ID
     * @return 日志详情
     */
    OptionLog selectById(@Param("id") String id);

    /**
     * 操作类型查询
     * @return 操作类型列表
     */
    List<String> operateOption();

    /**
     * 创建表 不存在才创建
     * @param year 年份
     * @return 创建结果
     */
    Integer createTable(@Param("year") int year);

    /**
     * 查询年份
     * @return 年份列表
     */
    List<String> operateYearOption();
}
