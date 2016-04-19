package com.chinamobile.iot.monitor.model;

import java.util.Date;

/**
 * Node 代表被监控系统的一台主机、一个功能或者其他信息
 *
 * @author szl
 */
public class Node {
    /**
     * 设备ID,OneNET中设备的唯一标识,大多数为DeviceId
     */
    private String id;
    /**
     * 设备名称
     */
    private String name;
    /**
     * 从OneNET提取设备数据的ApiKey
     */
    private String apiKey;
    /**
     * OneNET设备数据流的名称
     */
    private String streamId;
    /**
     * 数据提取的周期，单位秒
     */
    private int extractFreq;
    /**
     * 该设备上一次被提取的数据点的时间
     */
    private Date lastExtractTime;

    public Node() {

    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getApiKey() {
        return apiKey;
    }

    public void setApiKey(String apiKey) {
        this.apiKey = apiKey;
    }

    public String getStreamId() {
        return streamId;
    }

    public void setStreamId(String streamId) {
        this.streamId = streamId;
    }

    public int getExtractFreq() {
        return extractFreq;
    }

    public void setExtractFreq(int extractFreq) {
        this.extractFreq = extractFreq;
    }

    public Date getLastExtractTime() {
        return lastExtractTime;
    }

    public void setLastExtractTime(Date lastExtractTime) {
        this.lastExtractTime = lastExtractTime;
    }

}
