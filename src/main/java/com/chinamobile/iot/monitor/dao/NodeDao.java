package com.chinamobile.iot.monitor.dao;

import com.chinamobile.iot.monitor.model.*;
import org.apache.ibatis.annotations.Param;

import java.util.Date;
import java.util.List;

/**
 * MyBatis操作数据库的映射类
 */

public interface NodeDao {

    /**
     * 查询所有设备
     *
     * @return 设备列表
     */
    List<Node> selectNodes();

    /**
     * 查询指定nodeId的设备
     *
     * @param nodeId 设备Id
     * @return 所查询的单个设备
     */
    Node selectNode(@Param("id") String nodeId);

    /**
     * 更新指定nodeId对应的设备，更新其上次从OneNET提取数据的时间
     *
     * @param nodeId   设备Id
     * @param dateTime 时间
     */
    void updateLastReqTime(@Param("id") String nodeId,
                           @Param("time") Date dateTime);


    ///////////////////////////////////////////////////////////////////////////

    /**
     * 提取所有中间计算表达式
     *
     * @return 中间表达式列表
     */
    List<MiddleTarget> selectMidTargets();

    /**
     * 提取某个中间表达式
     *
     * @param name 中间表达式名称
     * @return 中间表达式
     */
    MiddleTarget selectMidTarget(@Param("name") String name);


    /**
     * 提取所有目标表达式
     *
     * @return 指标列表
     */
    List<Target> selectTargets();

    /**
     * 提取某个目标表达式
     *
     * @param name 目标表达式的名称
     * @return 指标属性
     */
    Target selectTarget(@Param("name") String name);

    /**
     * 提取某个终端设备需要计算的终端表达式名称
     *
     * @param nodeId 设备Id
     * @return 表达式名称列表
     */
    List<String> selectNodeTarget(@Param("id") String nodeId);


    /**
     * 列出所有设备和指标的映射关系
     *
     * @return 设备和指标的映射关系列表
     */
    List<NodeTarget> selectNodeTargets();


    /**
     * 向数据库表中插入一项计算出来的结果指标
     *
     * @param record 指标计算的结果
     */
    void insertTargetRecord(TargetRecord record);


    /**
     * 从数据库中查询某个设备、某个指标的最大时间
     *
     * @param nodeId     设备Id
     * @param targetName 指标名称
     * @return 本设备对应指标的最大时间
     */
    Date selectMaxDate(@Param("id") String nodeId, @Param("name") String targetName);
}
