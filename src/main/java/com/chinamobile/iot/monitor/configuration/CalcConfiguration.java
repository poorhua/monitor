package com.chinamobile.iot.monitor.configuration;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Created by szl on 2016/4/19.
 */
@ConfigurationProperties(prefix = "monitor.calc")
public class CalcConfiguration {
    /**
     * 计算线程池中线程的数量
     */
    private int threadcount;


    public int getThreadcount() {
        return threadcount;
    }

    public void setThreadcount(int threadcount) {
        this.threadcount = threadcount;
    }
}
