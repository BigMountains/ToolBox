package com.czl.tools.wechat.util;

import me.chanjar.weixin.common.error.WxErrorException;
import me.chanjar.weixin.mp.api.WxMpService;
import me.chanjar.weixin.mp.api.WxMpTemplateMsgService;
import me.chanjar.weixin.mp.bean.template.WxMpTemplateData;
import me.chanjar.weixin.mp.bean.template.WxMpTemplateMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.*;

@Component
public class WxTemplateUtil {
    private Logger log = LoggerFactory.getLogger(WxTemplateUtil.class);
    @Autowired
    private WxMpService wxMpService;

    /**
      * 推送消息给一位用户
      * @Author zhiliang
      * @Date 2019/7/15
      * @Email chenzl88@chinaunicom.cn
      * @Param
      * @return
      **/
    public void pushTemplateToOne(String templateId,
                                     String openId,
                                     String url,
                                     String first,
                                     String remark,
                                     String... keywords){
        pushTemplateToAll(templateId,new ArrayList<String>(0){
            {
                add(openId);
            }
        },url,first,remark,keywords);
    }


    /**
      * 推送模板给所有列表用户
      * @Author zhiliang
      * @Date 2019/7/15
      * @Email chenzl88@chinaunicom.cn
      * @Param
      * @return
      **/
    public void pushTemplateToAll(String templateId,
                                     List<String> openIds,
                                     String url,
                                     String first,
                                     String remark,
                                     String... keywords){
        WxMpTemplateMsgService templateService = wxMpService.getTemplateMsgService();
        WxMpTemplateMessage message = transTemplate(templateId,openIds,url,first,remark,keywords);
        for(Iterator it = openIds.iterator(); it.hasNext();){
            try {
                message.setToUser(it.next().toString());
                templateService.sendTemplateMsg(message);
            }catch (WxErrorException e){
                log.error("【模板推送失败】", e.getMessage(),it.next().toString());
            }
        }
    }


    /**
     * 根据type转换模板
     * @Author zhiliang
     * @Date 2019/4/23
     * @Email chenzl88@chinaunicom.cn
     * @Param
     * @return
     **/
    private WxMpTemplateMessage transTemplate(String templateId,
                                                    List<String> openIds,
                                                    String url,
                                                    String first,
                                                    String remark,
                                                    String... keywords){
        return createTemplate(
                openIds.get(0),
                templateId,
                url,
                null,
                new HashMap<String,String>(){
                    {
                        this.put("first",first);
                        this.put("remark",remark);
                        if(keywords.length != 0){
                            for(int i = 0; i<keywords.length; i++ ){
                                this.put("keyword"+(i+1),keywords[i]);
                            }
                        }
                    }
                }
        );
    }



    /**
     *  创建自定义模板消息
     **/
    private static WxMpTemplateMessage createTemplate(
            String openId,
            String templateId,
            String url,
            WxMpTemplateMessage.MiniProgram miniProgram,
            Map<String,String> data
    ){
        return WxMpTemplateMessage.builder()
                .toUser(openId)
                .templateId(templateId)
                .url(url)
                .miniProgram(miniProgram)
                .data(dataTransform(data))
                .build();
    }
    /**
     * 格式转换
     */
    private static List<WxMpTemplateData> dataTransform(Map<String,String> map){
        List<WxMpTemplateData> list = new ArrayList<>();
        for(Map.Entry entry:map.entrySet()){
            if(null != entry.getKey() && null !=entry.getValue()){
                list.add(new WxMpTemplateData(){
                    {
                        setName(entry.getKey().toString());
                        setValue(entry.getValue().toString());
                    }
                });
            }
        }
        return list;
    }

}
