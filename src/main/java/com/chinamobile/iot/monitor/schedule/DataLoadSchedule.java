package com.chinamobile.iot.monitor.schedule;

import com.chinamobile.iot.monitor.configuration.MonitorConfiguration;
import com.chinamobile.iot.monitor.dao.NodeDao;
import com.chinamobile.iot.monitor.dataload.OneNETDataLoader;
import com.chinamobile.iot.monitor.model.MiddleTarget;
import com.chinamobile.iot.monitor.model.Node;
import com.chinamobile.iot.monitor.model.NodeTarget;
import com.chinamobile.iot.monitor.model.Target;
import com.chinamobile.iot.monitor.observer.RabbitMQClient;
import com.chinamobile.iot.monitor.script.Expression;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.util.StopWatch;

import javax.annotation.PostConstruct;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 定时器
 * 定时从OneNET读取数据，读取后后台计算指标
 * Created by szl on 2016/3/24.
 */
@Component
public class DataLoadSchedule {

    private static final Logger logger = LoggerFactory.getLogger(DataLoadSchedule.class);
    /**
     * 所有的指标计算中间表达式
     */
    List<MiddleTarget> middleTargets;
    /**
     * 所有的指标计算最终表达式
     */
    List<Target> targets;
    /**
     * 设备和指标的映射关系
     */
    List<NodeTarget> nodeTargets;
    /**
     * 中间表达式映射表
     */
    HashMap<String, Expression> middleMap = new HashMap<String, Expression>();
    /**
     * 指标表达式映射表
     */
    HashMap<String, TargetWithExpr> targetMap = new HashMap<String, TargetWithExpr>();
    @Autowired
    private MonitorConfiguration monitorConfiguration;
    @Autowired
    private RabbitMQClient observer;

    /**
     * 用来执行子任务的线程池
     */
    @Autowired
    private CalcThreadPool pool;
    /**
     * 访问数据库的对象
     */
    @Autowired
    private NodeDao dao;
    /**
     * 从OneNET提取数据的对象
     */
    @Autowired
    private OneNETDataLoader loader;
    /**
     * 配置数据，在初始化会读取数据库的数据完成初始化，初始化完成后，会传递给每个执行线程
     */
    private CalConfig config;
    /**
     * 存储中间结果的Hash表，Key为deviceId
     */
    private ConcurrentHashMap<String, MiddleTargetData> middledataMap = new ConcurrentHashMap<String, MiddleTargetData>();

    /**
     * 每分钟执行一次
     */
    @Scheduled(fixedDelay = 5000, initialDelay = 2000)
    public void run() {
        // 与数据库里的配置对齐
        checkConfig();
        // 从数据库里提取设备Id列表, 对每个设备创建一个任务
        List<Node> nodes = dao.selectNodes();
        for (Node node : nodes) {
            // 如果当前设备正在后台线程处理过程中，等待下一次循环再进行处理
            if (config.deviceStatMap.containsKey(node.getId())) {
                continue;
            }
            CalcTask task = new CalcTask();
            task.setApiKey(node.getApiKey());
            task.setDeviceId(node.getId());
            task.setStartTime(node.getLastExtractTime());
            task.setConfig(config);
            task.setLoader(loader);
            task.setDao(dao);
            task.setMiddledataMap(middledataMap);
            pool.submit(task);
            config.deviceStatMap.putIfAbsent(node.getId(), 1);
        }
    }

    /**
     * 从数据库读取
     */
    @PostConstruct
    private void init() {
        logger.info("begin init schedule...");
        StopWatch watch = new StopWatch("init");
        watch.start();

        // 构造中间结果Hash表，名称-->表达式
        List<MiddleTarget> middleTargets = dao.selectMidTargets();
        // 构造所有中间结果表达式
        for (MiddleTarget middle : middleTargets) {
            Expression exp = new Expression();
            exp.init(middle.getName(), middle.getExpression());
            middleMap.put(middle.getName(), exp);
        }


        // 构造指标Hash表，名称-->表达式
        List<Target> targets = dao.selectTargets();
        // 构造所有指标结果表达式
        for (Target target : targets) {
            Expression exp = new Expression();
            exp.init(target.getName(), target.getExpression());
            TargetWithExpr t = new TargetWithExpr(exp);
            t.setName(target.getName());
            t.setTargetId(target.getTargetId());
            t.setExpression(target.getExpression());
            t.setPeriod(target.getPeriod());
            targetMap.put(target.getName(), t);
        }

        /**
         * 构造依据设备Id查询中间计算和指标计算表达式的方法
         */
        config = new CalConfig();
        config.setObserver(this.observer);
        initConfig();

        watch.stop();
        logger.info("init schedule completed! use {} ms", watch.getLastTaskTimeMillis());

    }


    public void checkConfig() {
        logger.info("begin check config...");
        StopWatch watch = new StopWatch("init");
        watch.start();
        List<MiddleTarget> middleTargets = dao.selectMidTargets();
        List<Target> targets = dao.selectTargets();
        HashMap<String, Expression> middleMap = new HashMap<String, Expression>();
        for (MiddleTarget mid : middleTargets) {
            Expression expr = this.middleMap.get(mid.getName());
            if (expr != null && expr.getExpr().equals(mid.getExpression())) {
                middleMap.put(mid.getName(), expr);
            } else {
                expr = new Expression();
                expr.init(mid.getName(), mid.getExpression());
                middleMap.put(mid.getName(), expr);
            }
        }
        this.middleMap.clear();
        this.middleMap = middleMap;

        HashMap<String, TargetWithExpr> targetMap = new HashMap<String, TargetWithExpr>();
        for (Target target : targets) {
            TargetWithExpr targetWithExpr = this.targetMap.get(target.getName());
            if (targetWithExpr != null && targetWithExpr.getExpression().equals(target.getExpression())) {
                targetWithExpr.setPeriod(target.getPeriod());
                targetWithExpr.setTargetId(target.getTargetId());
                targetMap.put(target.getName(), targetWithExpr);
            } else {
                targetWithExpr = new TargetWithExpr();
                targetWithExpr.setName(target.getName());
                targetWithExpr.setPeriod(target.getPeriod());
                targetWithExpr.setExpression(target.getExpression());
                targetWithExpr.setTargetId(target.getTargetId());
                targetWithExpr.setDescr(target.getDescr());

                Expression exp = new Expression();
                exp.init(target.getName(), target.getExpression());
                targetWithExpr.setExpr(exp);
                targetMap.put(target.getName(), targetWithExpr);
            }
        }
        this.targetMap.clear();
        this.targetMap = targetMap;
        initConfig();
        watch.stop();
        logger.info("check config completed. use {} ms", watch.getLastTaskTimeMillis());
    }


    private void initConfig() {
        // 提取设备和最终指标的映射关系
        ConcurrentHashMap<String, List<Expression>> nodeMiddleExprs = new ConcurrentHashMap<String, List<Expression>>();
        // 设备和指标表达式的映射
        ConcurrentHashMap<String, List<Expression>> nodeTargetExprs = new ConcurrentHashMap<String, List<Expression>>();

        ConcurrentHashMap<NodeTargetKey, Integer> targetPeroidMap = new ConcurrentHashMap<NodeTargetKey, Integer>();

        List<NodeTarget> nodeTargets = dao.selectNodeTargets();
        for (NodeTarget nodeTarget : nodeTargets) {
            TargetWithExpr e = targetMap.get(nodeTarget.getTargetName());
            if (e == null) {
                continue;
            }
            // 添加设备和指标表达式的映射关系
            List<Expression> targetExprs = getExpressionRef(nodeTargetExprs, nodeTarget.getNodeId());
            targetExprs.add(e.getExpr());
            targetPeroidMap.putIfAbsent(makeKey(nodeTarget.getNodeId(), e.getName()), e.getPeriod());

            // 添加设备和中间结果表达式的映射关系
            List<Expression> middleExprs = getExpressionRef(nodeMiddleExprs, nodeTarget.getNodeId());
            // 逐个提取指标表达式的参数列表，参数名应为中间表达式的名称
            List<String> params = e.getExpr().getParameters();
            for (String s : params) {
                if (middleMap.containsKey(s)) {
                    middleExprs.add(middleMap.get(s));
                }
            }
        }
        config.setMiddleExpMap(nodeMiddleExprs);
        config.setTargetExpMap(nodeTargetExprs);
        config.setTargetPeroidMap(targetPeroidMap);
    }


    /**
     * 从Hash表中提取链表队列的引用，如果不存在就创建，如果存在，就直接返回
     *
     * @param map 待处理的Map
     * @param key 待处理的List对应的Key
     * @return Key对应的链表
     */
    private List<Expression> getExpressionRef(ConcurrentHashMap<String, List<Expression>> map, String key) {
        if (map.containsKey(key)) {
            return map.get(key);
        } else {
            map.put(key, new LinkedList<Expression>());
            return map.get(key);
        }
    }

    /**
     * 依据设备Id和指标名创建一个NodeTargetKey对象
     *
     * @param nodeId     设备ID
     * @param targetName 指标名称
     * @return 依据设备Id、指标名查询时间周期的key
     */
    private NodeTargetKey makeKey(String nodeId, String targetName) {
        NodeTargetKey key = new NodeTargetKey();
        key.setNodeId(nodeId);
        key.setTargetName(targetName);
        return key;
    }
}
