package com.czl.tools.utils;

import com.alibaba.fastjson.JSONObject;
import okhttp3.*;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.charset.Charset;
import java.security.KeyStore;
import java.security.cert.CertificateFactory;
import java.util.HashMap;
import java.util.concurrent.TimeUnit;

@Component
public class OkHttpUtils implements InitializingBean {
    private static final MediaType JSON = MediaType.get("application/json; charset=utf-8");

    private OkHttpClient client;


    /**
     * 发送Post请求
     *
     * @return
     * @Author zhiliang
     * @Date 2019/7/19
     * @Email chenzl88@chinaunicom.cn
     * @Param
     **/
    public String sendPost(String url, HashMap<String, String> queryParam, JSONObject bodyParam, HashMap<String, String> headers) {
        String realUrl = buildUrlWithQueryParam(url, queryParam);
        Request.Builder requestBuilder = new Request.Builder();
        requestBuilder
                .post(RequestBody.create(bodyParam.toJSONString(), JSON))
                .url(realUrl);
        headers.forEach((k,v)->{
            requestBuilder.addHeader(k,v);
        });
        Request request = requestBuilder.build();
        try {
            Response response = client.newCall(request).execute();
            return new String(response.body().bytes(), Charset.defaultCharset());
        } catch (IOException e) {
            //TODO log
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 将信息带在Url后面
     **/
    private String buildUrlWithQueryParam(String url, HashMap<String, String> params) {
        if (params == null || params.isEmpty()) {
            return url;
        }
        StringBuffer sb = new StringBuffer(url);
        sb.append("?");
        params.forEach((k, v) -> {
            sb
                    .append(k)
                    .append("=")
                    .append(com.alibaba.fastjson.JSON.toJSONString(v))
                    .append("&");
        });
        sb.deleteCharAt(sb.length() - 1);
        return sb.toString();
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        OkHttpClient.Builder builder = new OkHttpClient.Builder();
        /** 设置超时时间 **/
        builder.connectTimeout(5, TimeUnit.SECONDS);
        builder.callTimeout(5, TimeUnit.SECONDS);
        builder.readTimeout(20, TimeUnit.SECONDS);
        builder.writeTimeout(20, TimeUnit.SECONDS);
        /** https请求设置 **/
        setCertificates(builder);
        client = builder.build();
    }

    /**
     * 设置证书
     **/
    private void setCertificates(OkHttpClient.Builder builder) throws Exception {
        CertificateFactory certificateFactory = CertificateFactory.getInstance("X.509");
        KeyStore keyStore = KeyStore.getInstance(KeyStore.getDefaultType());
        keyStore.load(null);
        int index = 0;
        // TODO 读取证书
    }


}
