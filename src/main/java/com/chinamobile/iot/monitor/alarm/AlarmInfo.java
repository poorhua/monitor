package com.chinamobile.iot.monitor.alarm;

import java.util.Date;

/**
 * 告警信息
 * 包括告警静态配置信息和告警处理过程中所产生的动态信息，如指标分析到的时间，指标异常的时间，告警的时间或者告警恢复的时间等等。
 * Created by szl on 2016/3/31.
 */
public class AlarmInfo {
    /**
     * Alarm配置信息
     */
    private AlarmConfig config;
    /**
     * 已经分析过的指标的最大时间
     */
    private Date targetAlalyzedTime;
    /**
     * 上次指标异常的时间，如果为null，表示指标一直正常
     */
    private Date AbnormalTime;
    /**
     * 上次次指标告警的时间，如果为null，还没触发告警
     */
    private Date AlarmTime;
    /**
     * 上一次告警消失的时间
     */
    private Date AlarmDispearTime;
}
