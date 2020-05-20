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
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
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
    @Bean
    public DataSource mainDataSource(){
        return DataSourceBuilder.create()
                .url(env.getProperty("ds.master.url"))
                .driverClassName(env.getProperty("ds.master.driver-class-name"))
                .username(env.getProperty("ds.master.username"))
                .password(env.getProperty("ds.master.password"))
                .build();
    }



    @Bean("dynamicDataSource")
    @Primary
    public DynamicDataSource dynamicDataSource()  {
        Map<Object,Object> dataSources = new HashMap<>();

        DataSource mainDataSource = mainDataSource();
        //设置主数据库
        dataSources.put("main",mainDataSource);

        //TODO 整合Mybatis 查询
        try {
            Connection conn = mainDataSource.getConnection();
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery("select * from source");
            while(rs.next()){
                //获取数据源信息
                String database = rs.getString("database");
                String host = rs.getString("host");
                String port = rs.getString("port");
                String username = rs.getString("username");
                String password = rs.getString("password");
                //构建数据源
                dataSources.put(database,DataSourceBuilder.create()
                        .url("jdbc:mariadb//" + host + ":" +port + "/" + database)
                        .username(username)
                        .password(password)
                        .driverClassName("org.mariadb.jdbc.Driver").build()
                );
            }
        }catch (SQLException e){
            e.printStackTrace();
        }

        DynamicDataSource dynamicDataSource = new DynamicDataSource();
        dynamicDataSource.setTargetDataSources(dataSources);
        return dynamicDataSource;
    }




}
