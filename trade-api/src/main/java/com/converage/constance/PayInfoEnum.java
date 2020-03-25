package com.converage.constance;

/**
 * Created by 旺旺 on 2020/3/20.
 */
public enum PayInfoEnum {
    BankPay("PayInfo"), WechatPay("WechatPay"), BankInfo("AliPay");


    private String type;

    PayInfoEnum(String type) {
        this.type = type;
    }

}
