package com.chinamobile.iot.monitor.observer;

import com.chinamobile.iot.monitor.configuration.MonitorConfiguration;
import com.chinamobile.iot.monitor.model.TargetRecord;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.utils.SerializationUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.util.Date;

/**
 * 向RabbitMQ发送指标计算结果的类。
 * 以广播的方式向RabbitMQ发送指标计算结果。
 * exchange的名字为picker， 类型为“fanout”
 * <p>
 * RabbitMQ监听的地址在application.properties中配置
 * monitor.host 监听的地址
 * monitor.port 监听的端口号
 * <p>
 * Created by szl on 2016/4/15.
 */
@Component
//@EnableConfigurationProperties(RabbitMQClientConfiguration.class)
public class RabbitMQClient implements NewTargetObserver {
    private static final Logger logger = LoggerFactory.getLogger(RabbitMQClient.class);
    private final static String EXCHANGE_NAME = "picker";
    //private ConnectionFactory factory;
    private Connection connection;
    private Channel channel;
    @Autowired
    private MonitorConfiguration config;

    @PostConstruct
    public void init() {
        if (!config.getRabbit().isEnable()) {
            return;
        }

        try {
            logger.info("begin init rabbitmq client: {}:{}", config.getRabbit().getHost(), config.getRabbit().getPort());
            ConnectionFactory factory = new ConnectionFactory();
            factory.setHost(config.getRabbit().getHost());
            factory.setPort(config.getRabbit().getPort());
            factory.setUsername(config.getRabbit().getUser());
            factory.setPassword(config.getRabbit().getPassword());
            factory.setAutomaticRecoveryEnabled(true);
            connection = factory.newConnection();
            channel = connection.createChannel();
            channel.exchangeDeclare(EXCHANGE_NAME, "fanout");
            logger.info("init rabbitmq client ok! exchangeName: {}, exchangeType: fanout, host: {}, port:{}", EXCHANGE_NAME, config.getRabbit().getHost(), config.getRabbit().getPort());
        } catch (Exception e) {
            logger.error("init rabbitmq client failed.");
            logger.error(e.getMessage());
        }
    }

    @PreDestroy
    public void destroy() {
        if (!config.getRabbit().isEnable()) {
            return;
        }
        try {
            if (channel != null) {
                channel.close();
            }
            if (connection != null) {
                connection.close();
            }
        } catch (Exception e) {
            logger.error(e.getMessage());
        }
    }

    @Override
    public void onNewTargetRecord(String nodeId, String targetName, String value, Date time) {
        if (!config.getRabbit().isEnable()) {
            return;
        }
        try {
            TargetRecord record = new TargetRecord();
            record.setTargetName(targetName);
            record.setTime(time);
            record.setResult(value);
            record.setNodeId(nodeId);
            channel.basicPublish(EXCHANGE_NAME, "", null, SerializationUtils.serialize(record));
            logger.info("send message to rabbitmq success. target={}", record.toString());
        } catch (Exception e) {
            logger.error("send message to rabbitmq failed!");
            logger.error(e.getMessage());
        }
    }
}
