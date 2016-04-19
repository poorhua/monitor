package com.chinamobile.iot.monitor.dataload;


import com.alibaba.fastjson.JSON;
import com.chinamobile.iot.monitor.configuration.MonitorConfiguration;
import com.chinamobile.iot.monitor.schedule.DeviceDataPoint;
import com.chinamobile.iot.monitor.util.DateUtil;
import com.chinamobile.onenet.sdk.DefaultOneNetClient;
import com.chinamobile.onenet.sdk.OneNetClient;
import com.chinamobile.onenet.sdk.OneNetException;
import com.chinamobile.onenet.sdk.entity.DataPoint;
import com.chinamobile.onenet.sdk.entity.DataStream;
import com.chinamobile.onenet.sdk.entity.SearchDataPointReq;
import com.chinamobile.onenet.sdk.entity.SearchDataPointRsp;
import com.chinamobile.onenet.sdk.request.SearchDataPointRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.text.ParseException;
import java.util.*;


/**
 * 从oneNET装载数据的实现类
 * 内部采用RestAPI来访问OneNET
 * Created by szl on 2016/3/24.
 */
@Component
public class OneNETDataLoader implements DataLoader {

    private final static Logger logger = LoggerFactory.getLogger(OneNETDataLoader.class);
    /**
     * 数据流的名称。在运营监控系统中数据流的名称是固定，在这里定位常量
     */
    private final static String STREAM_ID = "Picker";
    private final static String HOST = "api.heclouds.com";
    @Autowired
    private MonitorConfiguration config;
    /**
     * OneNET客户端
     */
    private OneNetClient client = null;

    @PostConstruct
    public void init() {

        if (config.getOnenet().getHost() == null) {
            logger.info("init OneNET client: {}", HOST);
            client = new DefaultOneNetClient(HOST);
        } else {
            logger.info("init OneNET client: {}", config.getOnenet().getHost());
            client = new DefaultOneNetClient(config.getOnenet().getHost());
        }
    }


    public List<DeviceDataPoint> loadData(String deviceId, String apiKey, Date start, Date end) {

        SearchDataPointRequest request = new SearchDataPointRequest();
        SearchDataPointReq req = new SearchDataPointReq();
        // 如果开始时间未设置，那么取开始时间30天前
        if (start == null) {
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(new Date());
            calendar.add(Calendar.DAY_OF_MONTH, -30);
            start = calendar.getTime();
        }
        req.setStart(start);

        /**
         * 如果设置了结束时间，就设置
         */
        if (end != null) {
            req.setEnd(end);
        }
        req.setDataStreamID(STREAM_ID);
        /**
         * 默认一次从OneNET返回最大3600个节点
         */
        req.setLimit(config.getOnenet().getLoadcount());
        request.setDeviceID(deviceId);
        request.setApiKey(apiKey);
        request.setEntity(req);

        try {
            SearchDataPointRsp rsp = client.execute(request);
            return convert(rsp);
        } catch (OneNetException e) {
            logger.warn("load data from OneNET failed, cause{}. deviceId={}, start={}, end={}", e.getMessage(), deviceId, start, end);
        }
        return null;
    }


    /**
     * 把OneNET数据点中查询出来的结果中value转换为map映射表，并存储到链表中
     *
     * @param rsp 从OneNET收到的响应包
     * @return 数据点列表，把OneNET响应包中的Value转换为Map
     */
    private List<DeviceDataPoint> convert(SearchDataPointRsp rsp) {
        if (rsp.getErrno() != 0) {
            return null;
        }

        if (rsp.getData() == null) {
            return null;
        }

        List<DataStream> streams = rsp.getData().getDatastreams();
        if (streams == null || streams.size() == 0) {
            return null;
        }

        List<DataPoint> datapoints = streams.get(0).getDatapoints();
        if (datapoints == null || datapoints.size() == 0) {
            return null;
        }

        List<DeviceDataPoint> result = new LinkedList<DeviceDataPoint>();
        for (DataPoint point : datapoints) {
            try {
                DeviceDataPoint dp = new DeviceDataPoint();
                Date date = DateUtil.parse(point.getAt());
                dp.setDate(date);
                // 把数据点的Value部分转换为MAP
                String strResult = (String) (point.getValue());

                Map<String, Object> map;
                map = JSON.parseObject(strResult);
                dp.setData(map);
                result.add(dp);
            } catch (ParseException e) {
                logger.error("parse data error! date:{}", point.getAt());
                logger.error(e.getMessage());
            }
        }
        return result;
    }

}
