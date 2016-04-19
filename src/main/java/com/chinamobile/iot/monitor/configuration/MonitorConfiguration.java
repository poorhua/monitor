package com.chinamobile.iot.monitor.configuration;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * Created by szl on 2016/4/19.
 */
@Configuration
@ConfigurationProperties(prefix = "monitor")
public class MonitorConfiguration {

    /**
     * Rabbit相关配置
     */
    private RabbitConfiguration rabbit;

    /**
     * 访问OneNET的相关配置
     */
    private OneNETConfiguration onenet;
    /**
     * 数据计算过程的相关配置
     */
    private CalcConfiguration calc;

    public RabbitConfiguration getRabbit() {
        return rabbit;
    }

    public void setRabbit(RabbitConfiguration rabbit) {
        this.rabbit = rabbit;
    }

    public OneNETConfiguration getOnenet() {
        return onenet;
    }

    public void setOnenet(OneNETConfiguration onenet) {
        this.onenet = onenet;
    }

    public CalcConfiguration getCalc() {
        return calc;
    }

    public void setCalc(CalcConfiguration calc) {
        this.calc = calc;
    }
}
