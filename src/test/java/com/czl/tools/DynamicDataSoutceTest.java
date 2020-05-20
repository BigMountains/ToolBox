package com.czl.tools;

import com.alibaba.fastjson.JSON;
import com.czl.tools.dynamic_datasource.Domain;
import com.czl.tools.dynamic_datasource.mapper.MyMapper;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

public class DynamicDataSoutceTest extends ToolsApplicationTests {


    @Autowired
    private MyMapper mapper;


    @Test
    public void test(){
        Domain result = mapper.selectAll();
        System.out.println(JSON.toJSONString(result));


    }
}
