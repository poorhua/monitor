package com.chinamobile.iot.monitor.dataload;

import com.chinamobile.iot.monitor.schedule.DeviceDataPoint;

import java.util.Date;
import java.util.List;

/**
 * OneNEtDataLoader负责从OneNET平台提取数据点
 * <p>
 * Created by szl on 2016/2/2.
 */
public interface DataLoader {

    /**
     * 从OneNet中提取某个设备一段时间内的所有数据点
     *
     * @param deviceId 设备ID
     * @param apiKey   设备的ApiKey
     * @param streamId 数据流Id
     * @param start    开始时间
     * @param end      终止时间   @return 数据点列表
     */
    List<DeviceDataPoint> loadData(String deviceId, String apiKey, String streamId, Date start, Date end);

}
