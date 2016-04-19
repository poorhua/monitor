package com.chinamobile.iot.monitor.schedule;

/**
 * Created by szl on 2016/3/25.
 */
public class NodeTargetKey {
    /**
     * 设备Id
     */
    private String nodeId;
    /**
     * 指标名
     */
    private String targetName;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof NodeTargetKey)) return false;

        NodeTargetKey that = (NodeTargetKey) o;

        if (!nodeId.equals(that.nodeId)) return false;
        return targetName.equals(that.targetName);

    }

    @Override
    public int hashCode() {
        int result = nodeId.hashCode();
        result = 31 * result + targetName.hashCode();
        return result;
    }

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