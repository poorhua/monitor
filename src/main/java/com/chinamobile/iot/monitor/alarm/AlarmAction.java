package com.chinamobile.iot.monitor.alarm;

/**
 * Alarm动作的接口，当触发一个告警或者告警恢复时，会触发此方法中的回调方法
 * <p>
 * Created by szl on 2016/3/31.
 */
public interface AlarmAction {

    void onAlarm(AlarmConfig alarm);

    void onAlarmDispear(AlarmConfig alarm);
}
