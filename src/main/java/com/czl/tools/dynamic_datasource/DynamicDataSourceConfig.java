package com.czl.tools.dynamic_datasource;

import com.czl.tools.utils.SpringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.core.env.Environment;

import javax.sql.DataSource;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Configuration
public class DynamicDataSourceConfig {


    @Autowired
    private Environment env;
    /**
     * 主数据库
     * @return
     */
//    @Bean
//    public DataSource mainDataSource(){
//        return DataSourceBuilder.create()
//                .url(env.getProperty("ds.master.url"))
//                .driverClassName(env.getProperty("ds.master.driver-class-name"))
//                .username(env.getProperty("ds.master.username"))
//                .password(env.getProperty("ds.master.password"))
//                .build();
//    }



    @Bean("dynamicDataSource")
    @Primary
    public DynamicDataSource dynamicDataSource(){
        Map<Object,Object> dataSources = new HashMap<>();

        dataSources.put("main",mainDataSource());

        DynamicDataSource dynamicDataSource = new DynamicDataSource();
        dynamicDataSource.setTargetDataSources(dataSources);
        return dynamicDataSource;
    }


    private DataSource mainDataSource(){
        return SpringUtils.registBean("mainDataSource",DataSource.class);
    }


}
