package com.chinamobile.iot.monitor.observer;

import java.util.Date;

/**
 * 指标计算的回调接口
 * 当计算线程完成一个指标的计算时，会回调此方法
 * Created by szl on 2016/3/29.
 */
public interface NewTargetObserver {

    /**
     * 当monitor完成某个指标计算时，会调用此方法完成数据处理
     *
     * @param nodeId     设备Id
     * @param targetName 指标名称
     * @param value      指标值
     * @param time       指标产生的时间
     */
    void onNewTargetRecord(String nodeId, String targetName, String value, Date time);

}
