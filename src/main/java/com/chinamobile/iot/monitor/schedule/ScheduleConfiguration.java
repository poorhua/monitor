package com.chinamobile.iot.monitor.schedule;


import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.SchedulingConfigurer;
import org.springframework.scheduling.config.ScheduledTaskRegistrar;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

/**
 * Spring task 定时器线程调度器的配置
 * 本线程调度器以固定的时间周期，扫描待处理的设备队列，生成设备任务并交由DataLoader线程进行处理
 * Created by szl on 2016/2/2.
 */

@Configuration
@EnableScheduling
public class ScheduleConfiguration implements SchedulingConfigurer {

    public void configureTasks(ScheduledTaskRegistrar taskRegistrar) {
        taskRegistrar.setScheduler(taskExecutor());
    }

    @Bean(destroyMethod = "shutdown")
    public Executor taskExecutor() {
        return Executors.newScheduledThreadPool(3);
    }
}
