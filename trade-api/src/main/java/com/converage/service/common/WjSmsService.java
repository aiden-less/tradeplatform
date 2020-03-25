package com.converage.service.common;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.NameValuePair;
import org.apache.commons.httpclient.methods.PostMethod;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.io.IOException;

/**
 * Created by bint on 2018/8/20.
 */
@Service
public class WjSmsService {

    private static final Logger logger = LoggerFactory.getLogger(WjSmsService.class);

    private static String Uid;
    private static String Key;

    @Autowired
    private GlobalConfigService globalConfigService;

    @PostConstruct
    private void init() {
//        Uid = globalConfigService.get(GlobalConfigService.Enum.WJ_SMS_UID);
//        Key = globalConfigService.get(GlobalConfigService.Enum.WJ_SMS_KEY);
    }


    /**
     * 发送短信
     *
     * @param phoneNumber 手机号
     * @param text        短信内容
     */
    public void sendSms(String phoneNumber, String text) {
        HttpClient client = new HttpClient();
        PostMethod post = new PostMethod("http://gbk.api.smschinese.cn");
        post.addRequestHeader("Content-Type", "application/x-www-form-urlencoded;charset=gbk");//在头文件中设置转码
        NameValuePair[] data = {
                new NameValuePair("Uid", Uid), new NameValuePair("Key", Key),
                new NameValuePair("smsMob", phoneNumber), new NameValuePair("smsText", text),
        };
        post.setRequestBody(data);
        String result = null;
        try {
            client.executeMethod(post);
            result = new String(post.getResponseBodyAsString().getBytes("utf-8"));
        } catch (IOException e) {
            logger.error(e.getMessage());
        }

        System.out.println("send msg result：" + result + ",phoneNumber：" + phoneNumber); //打印返回消息状态

        post.releaseConnection();
    }

    public static void main(String[] args) throws IOException {

    }

}
