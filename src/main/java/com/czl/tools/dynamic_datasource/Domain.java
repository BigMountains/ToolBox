package com.czl.tools.dynamic_datasource;

import lombok.Data;

@Data
public class Domain {

    private Integer id;

    private String host;

    private Integer port;

    private String username;

    private String password;

    private String database;

    private String params;


}
