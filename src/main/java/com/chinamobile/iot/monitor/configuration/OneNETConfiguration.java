package com.chinamobile.iot.monitor.configuration;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Created by szl on 2016/4/19.
 */
@ConfigurationProperties(prefix = "monitor.onenet")
public class OneNETConfiguration {
    /**
     * 访问OneNET的Host，默认为api.heclouds.com
     */
    private String host;

    /**
     * 每次从OneNET提取数据点的最大数目
     */
    private int loadcount;

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public int getLoadcount() {
        return loadcount;
    }

    public void setLoadcount(int loadcount) {
        this.loadcount = loadcount;
    }
}
