package com.chinamobile.iot.monitor.alarm;

/**
 * Alarm的配置信息。
 * 配置信息是指不会随着告警处理而发生变化的信息，可以理解为外部配置信息
 * Created by szl on 2016/3/31.
 */
public class AlarmConfig {
    /**
     * 告警名称
     */
    private String alarmName;
    /**
     * 告警描述
     */
    private String alarmDescr;
    /**
     * 告警的设备Id
     */
    private String nodeId;
    /**
     * 告警的指标名称
     */
    private String targetName;
    /**
     * 判别指标是否异常的表达式，如果表达式为true，表示异常
     */
    private String alarmCheckExpression;

    /**
     * 指标异常多长时间认为应产生告警，单位为秒
     */
    private int maxAbnormalPeriod;
    /**
     * 告警产生后，持续的不产生重复告警的时长，单位为秒
     */
    private int maxAlarmPeriod;
}
