package com.qkj.project.dao;

import com.qkj.project.entity.LocalMessage;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @author KeJiang Qi
 * @date 2026/2/12 - 10:01
 * @description
 */
@Mapper
public interface LocalMessageDao {

    /**
     * 插入本地消息
     * @param local 本地消息
     * @return 影响行数
     */
    int insert(LocalMessage local);

    /**
     * 更新消息状态
     * @param id 消息Id
     * @param status 状态
     * @return 更新条数
     */
    int updateStatus(@Param("id") String id, @Param("status") int status);

    /**
     * 查询需要发送的消息
     * @return 消息列表
     */
    List<LocalMessage> selectNeedSentMessage();

/**
     * 更新消息
     * @param msg 消息
     * @return 更新条数
     */
    int updateMessage(LocalMessage msg);

    /**
     * 更新消息状态为成功
     * @param id 消息Id
     * @return 更新条数
     */
    int updateStatusSuccess(@Param("id") String id);

    /**
     * 更新消息状态为失败
     * @param id 消息Id
     * @return 更新条数
     */
    int updateStatusFail(@Param("id") String id);

    /**
     * 查询消息是否已被消费
     * @param id 消息Id
     * @return 是否已被消费
     */
    boolean isConsumed(@Param("id") String id);

     /**
     * 标记消息为已消费
     * @param id 消息Id
     * @return 更新条数
     */
    int markConsumed(@Param("id") String id);
}
