package com.czl.tools.wechat.service;



public interface WeChatService {
    String validate(String timeStamp, String nonce, String echoStr, String signature);
    Object handleMessage(String timeStamp,
                         String nonce,
                         String signature,
                         String openId,
                         String msgSignature,
                         String encryptType,
                         String body);
}
