package com.chinamobile.iot.monitor.schedule;

import com.chinamobile.iot.monitor.configuration.MonitorConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * 执行具体任务的线程池
 * 默认每个任务处理一个设备的数据，有多少个任务，就有多少个设备
 * Created by szl on 2016/3/25.
 */
@Component
public class CalcThreadPool {

    private static final Logger logger = LoggerFactory.getLogger(CalcThreadPool.class);
    private static final int threadCount = 6;
    private ExecutorService executorService;
    @Autowired
    private MonitorConfiguration configuration;

    @PostConstruct
    public void init() {
        int count = configuration.getCalc().getThreadcount();
        if (count <= 0) {
            count = threadCount;
        }
        executorService = Executors.newFixedThreadPool(count);
        logger.info("init calc thread pool with count {}", count);
    }

    @PreDestroy
    public void destroy() {
        executorService.shutdown();
    }


    /**
     * 向线程池提交一个任务
     *
     * @param task 被提交的任务
     */
    public void submit(Runnable task) {
        executorService.submit(task);
    }
}
