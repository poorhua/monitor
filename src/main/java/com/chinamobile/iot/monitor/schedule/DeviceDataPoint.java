package com.chinamobile.iot.monitor.schedule;

import java.util.Date;
import java.util.Map;

public class DeviceDataPoint {
    /**
     * 表示数据点的时刻
     */
    private Date date;
    /**
     * 表示数据点中的数据，为一个Map
     */
    private Map<String, Object> data;

    public DeviceDataPoint() {
    }

    public DeviceDataPoint(Date date, Map<String, Object> map) {
        this.date = date;
        this.data = map;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public Map<String, Object> getData() {
        return data;
    }

    public void setData(Map<String, Object> data) {
        this.data = data;
    }
}
