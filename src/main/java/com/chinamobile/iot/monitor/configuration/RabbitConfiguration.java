package com.chinamobile.iot.monitor.configuration;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Created by szl on 2016/4/19.
 */
@ConfigurationProperties(prefix = "monitor.rabbit")
public class RabbitConfiguration {
    /**
     * RabbitMQ 监听的主机地址，IP地址或者主机名
     */
    private String host;
    /**
     * RabbitMQ监听的主机的端口号，默认为5672
     */
    private int port;
    /**
     * 使能标志，表示是否把数据发给RabbitMQ
     */
    private boolean enable;
    /**
     * 连接RabbitMQ的用户名
     */
    private String user;
    /**
     * 连接RabbitMQ的密码
     */
    private String password;

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public boolean isEnable() {
        return enable;
    }

    public void setEnable(boolean enable) {
        this.enable = enable;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
