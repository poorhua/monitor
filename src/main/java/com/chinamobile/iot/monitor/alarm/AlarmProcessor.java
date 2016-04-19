package com.chinamobile.iot.monitor.alarm;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * 告警处理器，内部有一个告警的动作队列
 * 当告警产生时，告警
 * Created by szl on 2016/3/31.
 */
public class AlarmProcessor {

    private ArrayList<AlarmAction> actions = new ArrayList<AlarmAction>(5);
    private Map<AlarmKey, AlarmInfo> mapAlarmInfo = new HashMap<AlarmKey, AlarmInfo>();


    public void init() {

    }

    public void addAlarmAction(AlarmAction action) {
        actions.add(action);
    }

    public void removeAlarmAction(AlarmAction action) {

    }

    public void onNewTargetRecord(String nodeId, String targetName, String value, Date time) {
        return;
    }
}
