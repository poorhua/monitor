package com.chinamobile.iot.monitor.model;

/**
 * 描述每个设备所关联到的Target
 * Created by szl on 2016/3/25.
 */
public class NodeTarget {
    /**
     * 设备Id
     */
    private String nodeId;
    /**
     * 指标名
     */
    private String targetName;

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
}
