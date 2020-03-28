package com.converage.utils;

import com.converage.architecture.exception.BusinessException;
import com.converage.entity.transaction.CctOrder;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.LinkedBlockingDeque;

public class CacheUtils {

    public static Map<String, String> smsSendCountMap = new ConcurrentHashMap<>(100);
    public static Map<String, String> withdrawCountMap = new ConcurrentHashMap<>(100);
    public static Map<String, String> rechargeCountMap = new ConcurrentHashMap<>(100);
    public static Map<String, String> certCountMap = new ConcurrentHashMap<>(100);



    //    public static List<User> allUserList = new ArrayList<>(1000);
    public static LinkedBlockingDeque<CctOrder> atoLinkedBlockingDeque = new LinkedBlockingDeque<>();


    public static void initSmsCountMap() {
        smsSendCountMap = new ConcurrentHashMap<>(100);
    }

    public static void putSmsCountMap(String phoneNumber, String msgCode) {
        String msgCodeCache = smsSendCountMap.get(phoneNumber);
        if (StringUtils.isNotEmpty(msgCodeCache)) {
            throw new BusinessException("发送短信次数达到上限,请稍后再试");
        }
        smsSendCountMap.put(phoneNumber, msgCode);
    }

    public static void initRechargeCountMap() {
        rechargeCountMap = new ConcurrentHashMap<>(100);
    }

    public static void putRechargeCountMap(String key) {
        String cache = rechargeCountMap.get(key);
        if (StringUtils.isNotEmpty(cache)) {
            throw new BusinessException("请勿重复操作");
        }
        rechargeCountMap.put(key, "1");
    }


    public static void initCertCountMap() {
        certCountMap = new ConcurrentHashMap<>(100);
    }

    public static void putCertCountMap(String key) {
        String cache = certCountMap.get(key);
        if (StringUtils.isNotEmpty(cache)) {
            throw new BusinessException("请稍后再申请");
        }
        withdrawCountMap.put(key, "1");
    }

    public static void initWithdrawCountMap() {
        withdrawCountMap = new ConcurrentHashMap<>(100);
    }

    public static void putWithDrawCountMap(String key) {
        String cache = withdrawCountMap.get(key);
        if (StringUtils.isNotEmpty(cache)) {
            throw new BusinessException("请勿重复提现");
        }
        withdrawCountMap.put(key, "1");
    }


    public static void addAto(CctOrder ato) {
        atoLinkedBlockingDeque.add(ato);
    }

    public static CctOrder pollAto() {
        return atoLinkedBlockingDeque.poll();
    }
}
