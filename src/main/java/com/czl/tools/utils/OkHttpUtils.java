package com.czl.tools.utils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.HashMultimap;
import okhttp3.*;
import org.apache.http.client.methods.RequestBuilder;
import org.apache.http.conn.ConnectTimeoutException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.net.SocketTimeoutException;
import java.util.HashMap;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

public class OkHttpUtils {
    private static Logger log = LoggerFactory.getLogger(OkHttpUtils.class);


    private static final MediaType URLENCODED = MediaType.parse("application/x-www-form-urlencoded;charset=utf-8");

    private static final MediaType JSON = MediaType.parse("application/json;charset=utf-8");

    private static final MediaType FORM_DATA = MediaType.parse("application/form-data;charset=utf-8");

    private static final MediaType FILE = MediaType.parse("application/octet-stream");
    /**
     * 持有一个客户端
     */
    private final static OkHttpClient client = new OkHttpClient.Builder()
            .connectTimeout(5, TimeUnit.SECONDS)
            .callTimeout(5,TimeUnit.SECONDS)
            .readTimeout(20,TimeUnit.SECONDS)
            .writeTimeout(20,TimeUnit.SECONDS)
            .build();


    /**
     * 发送Get请求
     * @param url
     * @param params
     * @param headers
     * @return
     */
    public static String get(String url, JSONObject params, JSONObject headers){
        return doGet(url,params,headers);
    }

    /**
     * 使用UrlEncoded编码发送
     * @param url
     * @param query
     * @return
     */
    public static String post(String url,JSONObject query,JSONObject header){
        return doPost(url,query,null,URLENCODED,header);
    }

    /**
     * 使用Json格式发出
     * @param url
     * @param body
     * @param header
     * @return
     */
    public static String postJson(String url,JSONObject body,JSONObject header){
        return doPost(url,body,null,JSON,header);
    }

    /**
     * 发送FormData 可发送文件
     * @param url
     * @param bodyParams
     * @return
     */
    public static String postFile(String url,HashMap<String,Object> bodyParams,JSONObject headers){
        return doPost(url,null,bodyParams,FORM_DATA,headers);
    }



    private static String doGet(String url, JSONObject params, JSONObject headers) {
        Request.Builder reqBuilder = new Request.Builder();
        reqBuilder.get();
        //设置请求头
        if(headers != null){
            headers.forEach((k,v)-> reqBuilder.addHeader(k,(String)v));
        }
        //设置参数  构造Url参数
        StringBuilder sb = new StringBuilder(url);
        sb.append("?");
        params.forEach((k,v)->{
            sb.append(k).append("=").append((String)v).append("&");
        });
        sb.deleteCharAt(sb.length()-1);
        reqBuilder.url(sb.toString());
        try {
            Response response = client.newCall(reqBuilder.build()).execute();
            return response.body().string();
        }catch (IOException e){
            log.error("http get request failed reason:",e);
            return null;
        }
    }

    /**
     * Post请求
     * @return
     */
    private static String doPost(String url, JSONObject bodyParam, HashMap<String,Object> formData, MediaType type,JSONObject headers) {
        Request.Builder reqBuilder = new Request.Builder();
        //设置Url
        reqBuilder.url(url);
        //设置Header
        if(headers != null){
            headers.forEach((k,v)-> reqBuilder.addHeader(k,(String)v));
        }
        //构造Body
        RequestBody body = null;
        //判断type参数  设置Body
        if (JSON.equals(type)) {
            body = RequestBody.create(bodyParam.toJSONString(),JSON);
        } else if (URLENCODED.equals(type)) {
            StringBuilder sb = new StringBuilder();
            bodyParam.forEach((k,v)-> sb.append(k).append("=").append((String)v).append("&"));
            sb.deleteCharAt(sb.length()-1);
            body = RequestBody.create(sb.toString(),URLENCODED);
        } else if (FORM_DATA.equals(type)) {
            MultipartBody.Builder bodyBuilder = new MultipartBody.Builder();
            bodyParam.forEach((k,v)-> {
                if(v instanceof File){
                    bodyBuilder.addFormDataPart(k,((File)v).getName(),
                            RequestBody.Companion.create((File)v,FILE));
                }else{
                    bodyBuilder.addFormDataPart(k,(String)v);
                }
            });
        } else{
            throw new IllegalArgumentException("unknown mediaType");
        }
        reqBuilder.post(body);
        try {
            Response response = client.newCall(reqBuilder.build()).execute();
            return response.body().string();
        }catch (IOException e){
            log.error("http post request failed reason:",e);
            return null;
        }
    }





}
