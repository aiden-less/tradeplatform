package com.converage.constance;

/**
 * Created by 旺旺 on 2020/3/19.
 */
public enum KlineTypeEnum {
    OneMinute("OneMinute"), FiveMinute("FiveMinute"), FifteenMinute("FifteenMinute"), ThirtyMinute("ThirtyMinute"),
    OneHour("OneHour"), FourHours("FourHours"), OneDay("OneDay"), OneWeek("OneWeek"), OneMonth("OneMonth");

    private String type;

    KlineTypeEnum(String type) {
        this.type = type;
    }

    public String getType() {
        return type;
    }
}
