package com.czl.tools.utils;

import com.alibaba.fastjson.JSONObject;
import lombok.AllArgsConstructor;
import lombok.Data;
import okhttp3.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Component;
import sun.plugin2.jvm.RemoteJVMLauncher;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Component
public class OkHttpUtils implements InitializingBean {
    private Logger log = LoggerFactory.getLogger(OkHttpUtils.class);
    private static final MediaType JSON = MediaType.get("application/json; charset=utf-8");
    private OkHttpClient client;
    
    /**
     * Post请求 仅URL参数与Headers
     **/
    public String syncSendPost(String url,
                               HashMap<String, String> urlParam,
                               HashMap<String, String> headers) {
        return syncSendPost(createPostRequest(url, urlParam, headers, null, null, null));
    }

    /**
     * Post请求  URL参数Header与Json
     **/
    public String syncSendPost(String url,
                               HashMap<String, String> urlParam,
                               HashMap<String, String> headers,
                               JSONObject jsonBody) {
        return syncSendPost(createPostRequest(url, urlParam, headers, jsonBody, null, null));
    }

    /**
     * Post请求 FormData
     **/
    public String syncSendPost(String url,
                               HashMap<String, String> urlParam,
                               HashMap<String, String> headers,
                               HashMap<String, String> formDataBody,
                               List<FileItem> files) {
        return syncSendPost(
                createPostRequest(url, urlParam, headers, null, formDataBody, files)
        );
    }

    /**
     * 异步Post请求 仅URL参数与Headers
     **/
    public void asyncSendPost(String url,
                              HashMap<String, String> urlParam,
                              HashMap<String, String> headers,
                              Callback callback) {
        asyncSendPost(createPostRequest(url, urlParam, headers, null, null, null),
                callback);
    }

    /**
     * 异步 Post请求  URL参数Header与Json
     **/
    public void asyncSendPost(String url,
                              HashMap<String, String> urlParam,
                              HashMap<String, String> headers,
                              JSONObject jsonBody,
                              Callback callback) {
        asyncSendPost(createPostRequest(url, urlParam, headers, jsonBody, null, null), callback);
    }

    /**
     * Post请求 FormData
     **/
    public void asyncSendPost(String url,
                              HashMap<String, String> urlParam,
                              HashMap<String, String> headers,
                              HashMap<String, String> formDataBody,
                              List<FileItem> files,
                              Callback callback) {
        asyncSendPost(
                createPostRequest(url, urlParam, headers, null, formDataBody, files), callback
        );
    }


    /**
     * 异步POST请求
     **/
    private void asyncSendPost(Request request, Callback callback) {
        Call call = client.newCall(request);
        call.enqueue(callback);
    }

    /**
     * 同步POST请求
     **/
    private String syncSendPost(Request request) {
        try {
            Response response = client.newCall(request).execute();
            return response.body().string();
        } catch (IOException e) {
            log.error("【网络请求失败】", e);
            return null;
        }
    }

    /**
     * 构建Post请求的Request
     **/
    private Request createPostRequest(String url,
                                      HashMap<String, String> urlParam,
                                      HashMap<String, String> headers,
                                      JSONObject jsonBody,
                                      HashMap<String, String> formDataBody,
                                      List<FileItem> files) {
        //构造带信息的URL
        String targetUrl = buildUrlWithQueryParam(url, urlParam);

        //构造Request
        Request.Builder requestBuilder = new Request.Builder();
        if (headers != null) {
            //添加Header
            headers.forEach(requestBuilder::addHeader);
        }
        //构造Body
        RequestBody body = null;
        if (jsonBody != null) {
            body = RequestBody.create(jsonBody.toJSONString(), JSON);
        } else {
            MultipartBody.Builder multipartBodyBuilder = new MultipartBody.Builder();
            if (formDataBody != null) {
                formDataBody.forEach(multipartBodyBuilder::addFormDataPart);
            }
            if (files != null) {
                files.forEach(i -> {
                    multipartBodyBuilder.addFormDataPart(
                            i.getParamName(),
                            i.getFileName(),
                            RequestBody.create(i.getFile(), i.getFileType())
                    );
                });
            }
        }
        if (body == null) {
            body = RequestBody.create(new byte[]{}, null);
        }
        return requestBuilder.url(targetUrl).post(body).build();
    }

    /**
     * 将信息带在Url后面
     **/
    private String buildUrlWithQueryParam(String url, HashMap<String, String> params) {
        if (params == null || params.isEmpty()) {
            return url;
        }
        if (url.contains("?")) {
            throw new IllegalArgumentException("传入的URL后不要携带参数");
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
        //TODO 校验最大长度
        return sb.toString();
    }


    /**
     * 文件请求的类,用于上传文件
     *
     * @Author zhiliang
     * @Date 2019/11/6
     * @Email chenzl88@chinaunicom.cn
     * @Param
     * @return
     **/
    @AllArgsConstructor
    @Data
    class FileItem {
        private String paramName;

        private String fileName;

        private File file;

        private MediaType fileType;

    }


    @Override
    public void afterPropertiesSet() throws Exception {
        OkHttpClient.Builder builder = new OkHttpClient.Builder();
        /** 设置超时时间 **/
        builder.connectTimeout(5, TimeUnit.SECONDS);
        builder.callTimeout(5, TimeUnit.SECONDS);
        builder.readTimeout(20, TimeUnit.SECONDS);
        builder.writeTimeout(20, TimeUnit.SECONDS);
        client = builder.build();
    }


}
