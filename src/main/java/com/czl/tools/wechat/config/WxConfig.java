package com.czl.tools.wechat.config;

import cn.binarywang.wx.miniapp.api.WxMaService;
import cn.binarywang.wx.miniapp.api.impl.WxMaServiceImpl;
import cn.binarywang.wx.miniapp.config.WxMaInMemoryConfig;
import me.chanjar.weixin.mp.api.WxMpInMemoryConfigStorage;
import me.chanjar.weixin.mp.api.WxMpMessageRouter;
import me.chanjar.weixin.mp.api.WxMpService;
import me.chanjar.weixin.mp.api.impl.WxMpServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;

@Configuration
public class WxConfig {

    @Autowired
    private Environment env;

    /** 微信公众号工具包 **/
    @Bean
    public WxMpService getWxMpService(){
        return new WxMpServiceImpl(){
            {
                setWxMpConfigStorage(new WxMpInMemoryConfigStorage(){
                    {
                        setAppId(env.getProperty("wechat.appId"));
                        setSecret(env.getProperty("wechat.appSecret"));
                        setToken(env.getProperty("wechat.token"));
                        setAesKey(env.getProperty("wechat.aesKey"));
                    }
                });
            }
        };
    }

    /**
     * 微信小程序工具包
     * @return
     */
    @Bean
    public WxMaService getWxMaService(){
        return new WxMaServiceImpl(){
            {
                setWxMaConfig(new WxMaInMemoryConfig(){
                    {
                        setAppid(env.getProperty("miniapp.appId"));
                        setSecret(env.getProperty("miniapp.appSecret"));
                        setToken(env.getProperty("miniapp.token"));
                        setAesKey(env.getProperty("miniapp.aesKey"));
                        setMsgDataFormat(env.getProperty("miniapp.msgDateFormat"));
                    }
                });
            }
        };
    }

    /**
      * 路由配置
      * @Author zhiliang
      * @Date 2019/7/12
      * @Email chenzl88@chinaunicom.cn
      * @Param
      * @return
      **/
    @Bean
    public WxMpMessageRouter getRouter(){
        return new WxMpMessageRouter(getWxMpService()){
            {
//                rule()
//                        .event(WxConsts.EventType.CLICK)
//                        .async(false)
//                        .handler(new ButtonClickHandler())
//                        .end();
            }
        };
    }

}
