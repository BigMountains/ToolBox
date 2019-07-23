package com.czl.tools.wechat.service;

import me.chanjar.weixin.mp.api.WxMpMessageRouter;
import me.chanjar.weixin.mp.api.WxMpService;
import me.chanjar.weixin.mp.bean.message.WxMpXmlMessage;
import me.chanjar.weixin.mp.bean.message.WxMpXmlOutMessage;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


/**
 * @author Leo
 */
@Service
public class WeChatServiceImpl implements WeChatService {
    private static final Logger log = LoggerFactory.getLogger(WeChatServiceImpl.class);
    /** 微信工具包 **/
    @Autowired
    private WxMpService wxMpService;
    @Autowired
    private WxMpMessageRouter wxRouter;

    /**
     * 处理微信消息
     *
     * @return
     * @Author 志亮
     * @Date 2018/11/12
     * @Email chenzl88@chinaunicom.cn
     * @Param
     **/
    @Override
    public Object handleMessage(String timeStamp,
                                String nonce,
                                String signature,
                                String openId,
                                String msgSignature,
                                String encryptType,
                                String xmlBody) {
        if (!wxMpService.checkSignature(timeStamp, nonce, signature)) {
            throw new IllegalArgumentException("非法请求");
        }
        WxMpXmlMessage inMessage = null;
        if (StringUtils.isBlank(encryptType)) {
            inMessage = WxMpXmlMessage.fromXml(xmlBody);
        } else if ("aes".equals(encryptType)) {
            inMessage = WxMpXmlMessage.fromEncryptedXml(xmlBody, wxMpService.getWxMpConfigStorage(), timeStamp, nonce, msgSignature);
        } else {
            log.error("不可识别的加密类型");
            return "";
        }
        log.debug("微信XML："+inMessage);
        WxMpXmlOutMessage outMessage = wxRouter.route(inMessage);
        if (outMessage != null) {
            if (StringUtils.isBlank(encryptType)) {
                return outMessage.toXml();
            } else if ("aes".equals(encryptType)) {
                return outMessage.toEncryptedXml(wxMpService.getWxMpConfigStorage());
            } else {
                log.error("不可识别的加密类型");
                return "";
            }
        }
        return null;
    }

    /**
     * 微信配置接口
     *
     * @return
     * @Author 志亮
     * @Date 2018/11/12
     * @Email chenzl88@chinaunicom.cn
     * @Param
     **/
    @Override
    public String validate(String timeStamp, String nonce, String echoStr, String signature) {
        if (StringUtils.isAnyBlank(timeStamp, signature, nonce, echoStr)) {
            throw new IllegalArgumentException("参数非法。");
        }
        if (wxMpService.checkSignature(timeStamp, nonce, signature)) {
            return echoStr;
        }
        return "请求非法";
    }




}
