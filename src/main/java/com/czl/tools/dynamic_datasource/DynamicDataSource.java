package com.czl.tools.dynamic_datasource;

import com.czl.tools.dynamic_datasource.mapper.MyMapper;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.datasource.lookup.AbstractRoutingDataSource;

public class DynamicDataSource extends AbstractRoutingDataSource {


    @Autowired
    private MyMapper mapper;

    @Override
    protected Object determineCurrentLookupKey() {
        return "datasource1";
    }






}
