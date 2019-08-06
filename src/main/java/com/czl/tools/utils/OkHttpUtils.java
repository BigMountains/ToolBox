package com.czl.tools.utils;

import com.alibaba.fastjson.JSONObject;
import com.google.gson.JsonObject;
import com.google.gson.annotations.JsonAdapter;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import okhttp3.*;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.entity.mime.FormBodyPartBuilder;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.security.KeyStore;
import java.security.cert.CertificateFactory;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@Component
public class OkHttpUtils implements InitializingBean {
    private static final MediaType JSON = MediaType.get("application/json; charset=utf-8");
    private OkHttpClient client;

    /**
     * 发送Get请求
     *
     * @return
     * @Author zhiliang
     * @Date 2019/8/6
     * @Email chenzl88@chinaunicom.cn
     * @Param
     **/
    public String sendGet(String url, HashMap<String, String> queryParam, Map<String, String> headers) throws IOException {
        String realUrl = buildUrlWithQueryParam(url, queryParam);

        Request.Builder requestBuilder = new Request.Builder();
        requestBuilder.get().url(realUrl);
        if (headers != null) {
            headers.forEach((k, v) -> requestBuilder.addHeader(k, v));
        }
        Request request = requestBuilder.build();
        Response response = client.newCall(request).execute();
        return new String(response.body().bytes(), Charset.defaultCharset());
    }


    public String sendFormPost(String url, HashMap<String,String> formParam) throws IOException{
        return sendPost(ParamType.Form,url,null,formParam,null,null);
    }
    public String sendJsonPost(String url, JSONObject jsonParam) throws IOException{
        return sendPost(ParamType.Json,url,null,null,jsonParam,null);
    }
    public String sendJsonPost(String url, HashMap<String,String> queryParam, JSONObject jsonParam) throws IOException{
        return sendPost(ParamType.Json,url,queryParam,null,jsonParam,null);
    }
    public String sendFilePost(String url, List<FileBody> fileBody) throws IOException{
        return sendPost(ParamType.FormData,url,null,null,null,fileBody);
    }
    public String sendFilePost(String url, HashMap<String,String> formParam, List<FileBody> fileBody) throws IOException{
        return sendPost(ParamType.FormData,url,null,formParam,null,fileBody);
    }




    /**
     * 发送Post请求
     *
     * @return
     * @Author zhiliang
     * @Date 2019/7/19
     * @Email chenzl88@chinaunicom.cn
     * @Param
     **/
    private String sendPost(ParamType type, String u, HashMap<String, String> queryParam, HashMap<String, String> formParam, JSONObject jsonParam, List<FileBody> fileBody) throws IOException {
        //构建URL
        String url = null;
        if (queryParam != null) {
            url = buildUrlWithQueryParam(u, queryParam);
        } else {
            url = url;
        }
        //构建RequestBody
        RequestBody body = null;
        switch (type) {
            case Json:
                if (jsonParam == null || "".equals(jsonParam)) {
                    throw new NullPointerException("JsonParam Cannot Be Null");
                }
                body = RequestBody.create(jsonParam.toJSONString(), JSON);
                break;
            case Form:
                if (formParam == null) {
                    throw new NullPointerException("FormParam Cannot Be Null");
                }
                FormBody.Builder formBodyBuilder = new FormBody.Builder();
                formParam.forEach((k, v) -> formBodyBuilder.addEncoded(k, v));
                body = formBodyBuilder.build();
                break;
            case FormData:
                MultipartBody.Builder multipartBuilder = new MultipartBody.Builder();
                multipartBuilder.setType(MultipartBody.FORM);
                if (formParam != null) {
                    formParam.forEach((k, v) -> multipartBuilder.addFormDataPart(k, v));
                }
                if (fileBody != null) {
                    fileBody.forEach(i -> {
                        multipartBuilder.addFormDataPart(
                                i.getName(),
                                i.getFileName(),
                                RequestBody.create(i.getFile(), i.getMediaType())
                        );
                    });
                }
                body = multipartBuilder.build();
                break;
            default:
                throw new NullPointerException("ParamType Cannot Be Null");
        }
        //构建Request
        Request.Builder requestBuilder = new Request.Builder();
        Request request = requestBuilder.post(body).url(url).build();
        //执行
        Response response = client.newCall(request).execute();
        return new String(response.body().bytes(), Charset.defaultCharset());
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

    /**
     * Post请求参数枚举类
     **/
    enum ParamType {
        Json, Form, FormData
    }

    @Getter
    @AllArgsConstructor
    class FileBody {
        private String name;
        private String fileName;
        private File file;
        private MediaType mediaType;
    }
}
