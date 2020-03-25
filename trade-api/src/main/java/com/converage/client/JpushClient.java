package com.converage.client;

import cn.jiguang.common.ClientConfig;
import cn.jiguang.common.resp.APIConnectionException;
import cn.jiguang.common.resp.APIRequestException;
import cn.jpush.api.JPushClient;
import cn.jpush.api.push.PushResult;
import cn.jpush.api.push.model.Message;
import cn.jpush.api.push.model.Platform;
import cn.jpush.api.push.model.PushPayload;
import cn.jpush.api.push.model.audience.Audience;
import cn.jpush.api.push.model.notification.Notification;
import com.converage.service.common.GlobalConfigService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

@Component
public class JpushClient {

    private static final Logger logger = LoggerFactory.getLogger(JpushClient.class);

    private static String appSecret = null;
    private static String appKey = null;

    @Autowired
    private GlobalConfigService globalConfigService;

    @PostConstruct
    public void init() {
//        appSecret = globalConfigService.get(GlobalConfigService.Enum.JPUSH_SECRET);
//        appKey = globalConfigService.get(GlobalConfigService.Enum.JPUSH_KEY);
    }


    public void pushMessage(String alias, String title, String message) {
        JPushClient pushClient = new JPushClient(appSecret, appKey, null, ClientConfig.getInstance());
        PushPayload payload = buildPushObject_alias_alert(alias, title, message);
        try {
            PushResult result = pushClient.sendPush(payload);
//            logger.info(result);
        } catch (APIConnectionException e) {
            // Connection error, should retry later
            logger.error("Connection error, should retry later", e);
        } catch (APIRequestException e) {
            // Should review the error, and fix the request
            logger.error("Should review the error, and fix the request", e);
            logger.info("HTTP Status: " + e.getStatus());
            logger.info("Error Code: " + e.getErrorCode());
            logger.info("Error Message: " + e.getErrorMessage());
        }
    }

    public PushPayload buildPushObject_alias_alert(String alias, String title, String message) {
        return PushPayload.newBuilder()
                .setPlatform(Platform.all())
                .setAudience(Audience.alias(alias))
                .setNotification(Notification.alert(title))
                .setMessage(Message.content(message))
                .build();
    }

    public void pushMessage(String title, String message) {
        JPushClient pushClient = new JPushClient(appSecret, appKey, null, ClientConfig.getInstance());
        PushPayload payload = buildPushObject_alias_alert(title, message);
        try {
            PushResult result = pushClient.sendPush(payload);
//            logger.info(result);
        } catch (APIConnectionException e) {
            // Connection error, should retry later
            logger.error("Connection error, should retry later", e);
        } catch (APIRequestException e) {
            // Should review the error, and fix the request
            logger.error("Should review the error, and fix the request", e);
            logger.info("HTTP Status: " + e.getStatus());
            logger.info("Error Code: " + e.getErrorCode());
            logger.info("Error Message: " + e.getErrorMessage());
        }
    }

    public PushPayload buildPushObject_alias_alert(String title, String message) {
        return PushPayload.newBuilder()
                .setPlatform(Platform.all())
                .setAudience(Audience.all())
                .setNotification(Notification.alert(title))
                .setMessage(Message.content(message))
                .build();
    }


}
