package com.chinamobile.iot.monitor.schedule;

import java.util.concurrent.ConcurrentHashMap;

/**
 * 存放中间结果的对象，每个设备有一个中间结果对象
 *
 * @author szl
 */
public class MiddleTargetData {
    /**
     * 存放中间结果的hash表,Key为中间值的名称，Value为计算的中间值
     */
    private ConcurrentHashMap<String, Object> middleResult = new ConcurrentHashMap<String, Object>();

    public ConcurrentHashMap<String, Object> getMiddleResult() {
        return middleResult;
    }

    public void setMiddleResult(ConcurrentHashMap<String, Object> middleResult) {
        this.middleResult = middleResult;
    }
}
