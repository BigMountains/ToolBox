package com.czl.tools.dynamic_datasource.mapper;

import com.czl.tools.dynamic_datasource.Domain;
import org.springframework.stereotype.Repository;

@Repository
public interface MyMapper{


    Domain selectAll();


}
