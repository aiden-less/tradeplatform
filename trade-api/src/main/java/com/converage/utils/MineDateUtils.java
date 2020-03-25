package com.converage.utils;

import org.apache.commons.lang.time.DateFormatUtils;

import java.sql.Timestamp;
import java.text.ParseException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.Calendar;
import java.util.Date;

public class MineDateUtils {
    public static final String YYYY_MM_DD_HH_MM_SS = "yyyy-MM-dd HH:mm:ss";

    /**
     * 指定日期加上天数后的日期
     * @param currentDate 创建时间
     * @param num 为增加的天数
     * @return
     * @throws ParseException
     */
    public static Timestamp plusDay(int num, Date currentDate){
        Calendar ca = Calendar.getInstance();
        ca.add(Calendar.DATE, num);
        currentDate = ca.getTime();
        return new Timestamp(currentDate.getTime());
    }


    /**
     * 获取今天剩余秒数
     * @return
     */
    public static long getDaySeconds () {
        // 当天最大时间
        LocalDateTime localDateTime = LocalDateTime.of(LocalDate.now(), LocalTime.MAX);
        return LocalDateTime.now().until(localDateTime, ChronoUnit.SECONDS);
    }

    public static String dateTimeFormat (Date date) {
        return DateFormatUtils.format(date, YYYY_MM_DD_HH_MM_SS);
    }

    public static String dateTimeFormat (Timestamp timestamp) {
        return DateFormatUtils.format(new Date(timestamp.getTime()), YYYY_MM_DD_HH_MM_SS);
    }

    public static String getCurDateFormat () {
        return DateFormatUtils.format(new Date(), YYYY_MM_DD_HH_MM_SS);
    }

}
