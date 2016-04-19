package com.chinamobile.iot.monitor.schedule;

import com.chinamobile.iot.monitor.dao.NodeDao;
import com.chinamobile.iot.monitor.dataload.DataLoader;
import com.chinamobile.iot.monitor.model.TargetRecord;
import com.chinamobile.iot.monitor.script.Expression;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 数据读取线程
 * 负责从oneNET读取数据
 * 负责对数据进行缓存
 * 负责对读取的数据进行排序
 * Created by szl on 2016/2/2.
 */
public class CalcTask implements Runnable {

    public static final Logger logger = LoggerFactory.getLogger(CalcTask.class);

    private CalConfig config;

    private DataLoader loader;

    private NodeDao dao;

    /**
     * 存储中间结果的Hash表，Key为deviceId
     */
    private ConcurrentHashMap<String, MiddleTargetData> middledataMap;


    //////////////////////////////////////////////////////////////////
    /**
     * 从OneNET提取数据的开始时间,如果为null，则会从一个月前开始提取
     */
    private Date startTime;
    /**
     * 本线程处理的设备Id
     */
    private String nodeId;
    /**
     * 本次提取数据的ApiKey
     */
    private String apiKey;
    //////////////////////////////////////////////////////////////////


    public CalcTask() {
    }

    public void run() {
        // 1 从设备云提取数据
        logger.info("begin process device {}, start time: {}", nodeId, startTime);

        Date maxDate = null;
        List<DeviceDataPoint> datas = loader.loadData(nodeId, apiKey, startTime, null);
        if (datas == null) {
            config.deviceStatMap.remove(nodeId);
            logger.info("load data failed!");
            return;
        }
        logger.info("load data from OneNET ok!");

        // 2 逐个处理数据点,计算中间结果,记录
        List<Expression> expsMiddle = config.getMiddleExpression(nodeId);
        List<Expression> expsTarget = config.getTargetExpression(nodeId);
        MiddleTargetData middledata = middledataMap.get(nodeId);

        if (middledata == null) {
            middledata = new MiddleTargetData();
            middledataMap.put(nodeId, middledata);
        }


        // 存储最终结果的临时表
        HashMap<String, Object> targetMap = new HashMap<String, Object>();
        logger.info("begin script middle target...");
        for (DeviceDataPoint point : datas) {
            // 记录本次提取的数据点中最大的时间
            if (maxDate == null) {
                maxDate = point.getDate();
            } else {
                if (maxDate.before(point.getDate())) {
                    maxDate = point.getDate();
                }
            }
            // 计算中间结果
            if (expsMiddle == null) {
                continue;
            }

            for (Expression exp : expsMiddle) {
                if (exp.canExec(point.getData())) {
                    exp.exec(point.getData(), middledata.getMiddleResult());
                }
            }

            //计算最终结果
            for (Expression e : expsTarget) {
                Date date = config.getTargetOutDate(nodeId, e.getName());
                if (date == null) {
                    Date d = dao.selectMaxDate(nodeId, e.getName());
                    Calendar calendar = Calendar.getInstance();
                    if (d != null) {
                        calendar.setTime(d);
                    } else {
                        calendar.setTime(point.getDate());
                    }
                    int peroid = config.getTargetOutputPeriod(nodeId, e.getName());
                    calendar.add(Calendar.SECOND, peroid);
                    date = calendar.getTime();
                    config.setTargetOutDate(nodeId, e.getName(), date);
                }
                // 判断是否达到最终指标输出条件
                int ret = canExecTarget(point.getDate(), date, config.getTargetOutputPeriod(nodeId, e.getName()));
                switch (ret) {
                    case 0: {
                        logger.info("begin script target...");
                        if (e.canExec(middledata.getMiddleResult())) {
                            // 计算最终指标
                            if (0 == e.exec(middledata.getMiddleResult(), targetMap)) {
                                // 计算计算结果存入数据库
                                TargetRecord record = new TargetRecord();
                                record.setTime(date);
                                record.setNodeId(nodeId);
                                record.setTargetName(e.getName());
                                record.setResult(targetMap.get(e.getName()).toString());
                                logger.info("store the target record into database, targetRecord={}", record.toString());
                                if (config.getObserver() != null) {
                                    config.getObserver().onNewTargetRecord(nodeId, e.getName(), record.getResult(), record.getTime());
                                }
                                dao.insertTargetRecord(record);
                            }
                        }
                        // 清空本次计算的中间结果
                        for (String s : e.getParameters()) {
                            if (middledata.getMiddleResult().containsKey(s)) {
                                middledata.getMiddleResult().remove(s);
                            }
                        }
                        // 设置下一次计算指标的时间
                        Calendar calendar = Calendar.getInstance();
                        calendar.setTime(date);
                        calendar.add(Calendar.SECOND, config.getTargetOutputPeriod(nodeId, e.getName()));
                        date = calendar.getTime();
                        // 如果下一次时间的时间还没有当前数据点的时间大，则更新
                        if (point.getDate().after(date)) {
                            calendar.setTime(point.getDate());
                            calendar.add(Calendar.SECOND, config.getTargetOutputPeriod(nodeId, e.getName()));
                            date = calendar.getTime();
                        }
                        config.setTargetOutDate(nodeId, e.getName(), date);
                    }
                    break;
                    case 1: {
                        // 设置下一次计算指标的时间
                        Calendar calendar = Calendar.getInstance();
                        calendar.setTime(point.getDate());
                        calendar.add(Calendar.SECOND, config.getTargetOutputPeriod(nodeId, e.getName()));
                        date = calendar.getTime();
                        config.setTargetOutDate(nodeId, e.getName(), date);
                    }
                    break;
                    case -1: {

                    }
                    break;
                }
            }
        }
        // 更新数据库表中本设备访问到的最新的时刻
        if (maxDate != null) {
            // 设置下一秒为访问该设备的开始时间
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(maxDate);
            calendar.add(Calendar.SECOND, 1);
            maxDate = calendar.getTime();
            dao.updateLastReqTime(nodeId, maxDate);
        }

        config.deviceStatMap.remove(nodeId);
        logger.info("process device completed! deviceID: {}, start time: {}", nodeId, startTime);

        return;
    }

    /**
     * 判断是否该输出最终指标
     * 如果数据点的时间小于期望的时间，则返回-1；
     * 如果数据点的时间大于期望时间，但是小于期望时间+指标输出间隔，则返回0
     * 如果数据点的时间大于期望时间+指标输出间隔，则返回1。
     *
     * @param dataDate     数据点的时间
     * @param expectedDate 期望输出指标的时间
     * @param secondOffset 输出指标的间隔
     * @return 0 表示应该输出指标值，1表示数据点的时间和数据库的时间跨度很大，应重设下一次计算指标的时间，-1表示时间还未到计算最终指标的阶段。
     */
    private int canExecTarget(Date dataDate, Date expectedDate, int secondOffset) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(expectedDate);
        calendar.add(Calendar.SECOND, secondOffset);
        Date dataWindow = calendar.getTime();

        if (dataDate.before(expectedDate)) {
            return -1;
        } else {
            if (dataDate.after(dataWindow)) {
                return 1;
            } else {
                return 0;
            }
        }
    }

    public Date getStartTime() {
        return startTime;
    }

    public void setStartTime(Date startTime) {
        this.startTime = startTime;
    }

    public String getDeviceId() {
        return nodeId;
    }

    public void setDeviceId(String deviceId) {
        this.nodeId = deviceId;
    }

    public String getApiKey() {
        return apiKey;
    }

    public void setApiKey(String apiKey) {
        this.apiKey = apiKey;
    }

    public CalConfig getConfig() {
        return config;
    }

    public void setConfig(CalConfig config) {
        this.config = config;
    }

    public DataLoader getLoader() {
        return loader;
    }

    public void setLoader(DataLoader loader) {
        this.loader = loader;
    }

    public ConcurrentHashMap<String, MiddleTargetData> getMiddledataMap() {
        return middledataMap;
    }

    public void setMiddledataMap(ConcurrentHashMap<String, MiddleTargetData> middledataMap) {
        this.middledataMap = middledataMap;
    }

    public NodeDao getDao() {
        return dao;
    }

    public void setDao(NodeDao dao) {
        this.dao = dao;
    }
}
