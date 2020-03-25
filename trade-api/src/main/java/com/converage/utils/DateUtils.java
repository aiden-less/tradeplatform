package com.converage.utils;

import com.converage.architecture.exception.BusinessException;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class DateUtils {
    public static String dateToWeek(Date date) throws java.text.ParseException {
        SimpleDateFormat f = new SimpleDateFormat("yyyy-MM-dd");
        String[] weekDays = {"周日", "周一", "周二", "周三", "周四", "周五", "周六"};
        Calendar cal = Calendar.getInstance(); // 获得一个日历
        Date datet = f.parse(f.format(date));
        cal.setTime(datet);
        int w = cal.get(Calendar.DAY_OF_WEEK) - 1; // 指示一个星期中的某天。
        if (w < 0)
            w = 0;
        return weekDays[w];
    }


    public static Integer getWeekNoFromWeekStr(String weekStr) {
        switch (weekStr) {
            case "周一":
                return 1;
            case "周二":
                return 2;
            case "周三":
                return 3;
            case "周四":
                return 4;
            case "周五":
                return 5;
            case "周六":
                return 6;
            case "周日":
                return 7;

            default:
                throw new BusinessException("参数异常");
        }
    }

    public static void main(String[] args) throws java.text.ParseException {
        System.out.println(dateToWeek(new Date()));
//        System.out.println(new Date());
    }
}
