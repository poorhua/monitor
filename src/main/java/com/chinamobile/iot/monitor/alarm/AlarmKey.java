package com.chinamobile.iot.monitor.alarm;

/**
 * 告警信息初始化后，在Hash表中存储，AlarmKey为HashMap中的Key的类型
 * Created by szl on 2016/3/31.
 */
public class AlarmKey {
    /**
     * 设备Id
     */
    private String nodeId;
    /**
     * 指标名称
     */
    private String targetName;

    public AlarmKey() {
    }

    public AlarmKey(String nodeId, String targetName) {
        this.nodeId = nodeId;
        this.targetName = targetName;
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof AlarmKey)) return false;

        AlarmKey alarmKey = (AlarmKey) o;

        if (!nodeId.equals(alarmKey.nodeId)) return false;
        return targetName.equals(alarmKey.targetName);

    }

    @Override
    public int hashCode() {
        int result = nodeId.hashCode();
        result = 31 * result + targetName.hashCode();
        return result;
    }
}
