package com.chinamobile.iot.monitor.model;

import java.io.Serializable;
import java.util.Date;

public class TargetRecord implements Serializable {
    /**
     * 序列化版本号
     */
    private static final long serialVersionUID = 1234567890;
    /**
     * 设备Id
     */
    private String nodeId;
    /**
     * 指标Id
     */
    private String targetName;
    /**
     * 指标结果
     */
    private String result;
    /**
     * 指标结果的时间
     */
    private Date time;

    public String getNodeId() {
        return nodeId;
    }

    public void setNodeId(String nodeId) {
        this.nodeId = nodeId;
    }

    public String getTargetName() {
        return targetName;
    }

    public void setTargetName(String targetName) {
        this.targetName = targetName;
    }

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }

    public Date getTime() {
        return time;
    }

    public void setTime(Date time) {
        this.time = time;
    }

    @Override
    public String toString() {
        return "TargetRecord{" +
                "nodeId='" + nodeId + '\'' +
                ", targetName='" + targetName + '\'' +
                ", result='" + result + '\'' +
                ", time=" + time +
                '}';
    }
}
