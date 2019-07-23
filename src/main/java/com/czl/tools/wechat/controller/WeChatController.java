package com.czl.tools.wechat.controller;

import com.czl.tools.wechat.service.WeChatService;
import me.chanjar.weixin.mp.api.WxMpService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@CrossOrigin("*")
@Controller
@RequestMapping("/wechat")
public class WeChatController {

    @Autowired
    private WeChatService weChatService;


    /**
     * 判断微信请求
     *
     * @return
     * @Author 志亮
     * @Date 2018/11/12
     * @Email chenzl88@chinaunicom.cn
     * @Param
     **/
    @GetMapping(value = "/receive", produces = "text/plain;charset=UTF-8")
    @ResponseBody
    public String validate(String timestamp,
                           String nonce,
                           String echostr,
                           String signature) {
        return this.weChatService.validate(timestamp, nonce, echostr, signature);
    }

    /**
     * 处理微信请求
     *
     * @return
     * @Author 志亮
     * @Date 2018/11/12
     * @Email chenzl88@chinaunicom.cn
     * @Param
     **/
    @PostMapping("/receive")
    @ResponseBody
    public Object handleMessage(@RequestParam("timestamp") String timeStamp,
                                @RequestParam("nonce") String nonce,
                                @RequestParam("signature") String signature,
                                @RequestParam("openid") String openId,
                                @RequestParam(value = "msg_signature", required = false) String msgSignature,
                                @RequestParam(value = "encrypt_type", required = false) String encryptType,
                                @RequestBody String body) {
        return this.weChatService.handleMessage(timeStamp, nonce, signature, openId, msgSignature, encryptType, body);
    }


}
