package com.chinamobile.iot.monitor;

import org.apache.ibatis.io.Resources;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.IOException;
import java.io.Reader;

/**
 * MyBatis 的 配置文件
 * 主要配置文件在mybatis.xml文件中
 */
@Configuration
public class MyBatisConfiguration {

    @Bean
    SqlSessionFactory createSqlSessionFactory() throws IOException {
        String resource = "mybatis.xml";
        Reader reader = Resources.getResourceAsReader(resource);
        SqlSessionFactoryBuilder builder = new SqlSessionFactoryBuilder();
        SqlSessionFactory factory = builder.build(reader);
        return factory;
    }
}
