package com.chinamobile.iot.monitor.schedule;

import com.chinamobile.iot.monitor.observer.NewTargetObserver;
import com.chinamobile.iot.monitor.script.Expression;

import java.util.Date;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * 指标计算的配置类
 * 指标计算后台采用多线程计算，这些线程共用同一个CalConfig对象
 * Created by szl on 2016/2/2.
 */
public class CalConfig {

    /**
     * 设备状态并发Map，如果本Map中存在，即表示设备正在后台线程处理中，如果不存在，则表示没有后台线程正在处理本设备
     */
    public ConcurrentHashMap<String, Integer> deviceStatMap = new ConcurrentHashMap<String, Integer>();
    private NewTargetObserver observer;
    /**
     * 同步对象
     */
    private ReentrantReadWriteLock lock = new ReentrantReadWriteLock();
    /**
     * key为NodeId，Value为一个计算中间值的表达式链表，主要记录某个设备要计算哪些表达式
     */
    private ConcurrentHashMap<String, List<Expression>> middleExpMap;
    /**
     * key为NodeId，Value为计算最终结果的表达式，主要用来记录某个设备需要计算哪些最终指标
     */
    private ConcurrentHashMap<String, List<Expression>> targetExpMap;
    /**
     * 存储每个设备输出指标的间隔，单位为秒, key为指标Id
     */
    private ConcurrentHashMap<NodeTargetKey, Integer> targetPeroidMap;
    /**
     * 存储每个设备的每个指标下一次计算输出的时间，Key包括设备Id和指标名
     */
    private ConcurrentHashMap<NodeTargetKey, Date> targetNextDateMap = new ConcurrentHashMap<NodeTargetKey, Date>();

    public NewTargetObserver getObserver() {
        return observer;
    }

    public void setObserver(NewTargetObserver observer) {
        this.observer = observer;
    }

    public ConcurrentHashMap<String, List<Expression>> getMiddleExpMap() {
        ConcurrentHashMap<String, List<Expression>> result;
        lock.readLock().lock();
        result = middleExpMap;
        lock.readLock().unlock();
        return result;
    }

    public void setMiddleExpMap(ConcurrentHashMap<String, List<Expression>> middleExpMap) {
        lock.writeLock().lock();
        this.middleExpMap = middleExpMap;
        lock.writeLock().unlock();
    }

    public ConcurrentHashMap<String, List<Expression>> getTargetExpMap() {
        ConcurrentHashMap<String, List<Expression>> result;
        lock.readLock().lock();
        result = targetExpMap;
        lock.readLock().unlock();
        return result;
    }

    public void setTargetExpMap(ConcurrentHashMap<String, List<Expression>> targetExpMap) {
        lock.writeLock().lock();
        this.targetExpMap = targetExpMap;
        lock.writeLock().unlock();
    }

    public ConcurrentHashMap<NodeTargetKey, Integer> getTargetPeroidMap() {
        ConcurrentHashMap<NodeTargetKey, Integer> result;
        lock.readLock().lock();
        result = targetPeroidMap;
        lock.readLock().unlock();
        return result;
    }

    public void setTargetPeroidMap(ConcurrentHashMap<NodeTargetKey, Integer> targetPeroidMap) {
        lock.writeLock().lock();
        this.targetPeroidMap = targetPeroidMap;
        lock.writeLock().unlock();
    }

    public ConcurrentHashMap<NodeTargetKey, Date> getTargetNextDateMap() {
        ConcurrentHashMap<NodeTargetKey, Date> result;
        lock.readLock().lock();
        result = targetNextDateMap;
        lock.readLock().unlock();
        return result;
    }

    public void setTargetNextDateMap(ConcurrentHashMap<NodeTargetKey, Date> targetNextDateMap) {
        lock.writeLock().lock();
        this.targetNextDateMap = targetNextDateMap;
        lock.writeLock().unlock();
    }


    /**
     * 提取对于某个设备应该计算哪些中间结果，通过分析表达式得到
     *
     * @param deviceId
     * @return
     */
    public List<Expression> getMiddleExpression(String deviceId) {
        return getMiddleExpMap().get(deviceId);
    }

    /**
     * 提取某个设备应该计算的最终指标
     *
     * @param deviceId
     * @return
     */
    public List<Expression> getTargetExpression(String deviceId) {
        return getTargetExpMap().get(deviceId);
    }

    /**
     * 得到某个设备某个指标的下次输出时间
     *
     * @param deviceId   设备Id
     * @param targetName 指标名称
     * @return 设备下次应该输出的时间
     */
    public Date getTargetOutDate(String deviceId, String targetName) {
        NodeTargetKey key = new NodeTargetKey();
        key.setTargetName(targetName);
        key.setNodeId(deviceId);
        Date result = getTargetNextDateMap().get(key);
        return result;
    }

    /**
     * 设置某个设备某个指标下次指标输出的时间
     *
     * @param deviceId   设备Id
     * @param targetName 指标名称
     * @param date       下次指标输出的时间
     */
    public void setTargetOutDate(String deviceId, String targetName, Date date) {
        NodeTargetKey key = new NodeTargetKey();
        key.setTargetName(targetName);
        key.setNodeId(deviceId);
        getTargetNextDateMap().put(key, date);
    }

    /**
     * 得到设备输出的时间间隔，单位为秒
     *
     * @param deviceId   设备Id
     * @param targetName 设备指标名称
     * @return 指标输出间隔
     */
    public int getTargetOutputPeriod(String deviceId, String targetName) {
        NodeTargetKey key = new NodeTargetKey();
        key.setTargetName(targetName);
        key.setNodeId(deviceId);
        return getTargetPeroidMap().get(key);
    }
}
