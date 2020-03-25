package com.converage.utils;

import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * Created by bint on 2018/8/25.
 */
public class BigDecimalUtils {

    private static final BigDecimal hundredBigDecimal = BigDecimal.valueOf(100);


    /**
     * 相乘
     *
     * @param bigDecimal1
     * @param bigDecimal2
     * @return
     */
    public static BigDecimal multiply(BigDecimal bigDecimal1, BigDecimal bigDecimal2) {
        return bigDecimal1.multiply(bigDecimal2);
    }

    /**
     * 转为负数
     *
     * @param bigDecimal1
     * @return
     */
    public static BigDecimal transfer2Negative(BigDecimal bigDecimal1) {
        return bigDecimal1.multiply(BigDecimal.valueOf(-1));
    }

    /**
     * 相加
     *
     * @param bigDecimal1
     * @param bigDecimal2
     * @return
     */
    public static BigDecimal add(BigDecimal bigDecimal1, BigDecimal bigDecimal2) {
        return bigDecimal1.add(bigDecimal2);
    }


    public static BigDecimal divide(BigDecimal bigDecimal1, BigDecimal bigDecimal2) {
        return bigDecimal1.divide(bigDecimal2, SCALE_SIX, RoundingMode.HALF_UP);
    }

    /**
     * 相除
     *
     * @param bigDecimal1
     * @param bigDecimal2
     * @return
     */
    public static BigDecimal divide(BigDecimal bigDecimal1, BigDecimal bigDecimal2, Integer SCALE_LENGTH) {
        return bigDecimal1.divide(bigDecimal2, SCALE_LENGTH, RoundingMode.HALF_UP);
    }

    /**
     * 百分比转比例
     *
     * @param bigDecimal
     * @return
     */
    public static BigDecimal percentToRate(BigDecimal bigDecimal) {
        return divide(bigDecimal, hundredBigDecimal, SCALE_SIX);
    }

    public static BigDecimal percentToRate(int value) {
        return BigDecimal.valueOf(value).divide(hundredBigDecimal);
    }

    /**
     * 相减
     *
     * @param bigDecimal1
     * @param bigDecimal2
     * @return
     */
    public static BigDecimal subtract(BigDecimal bigDecimal1, BigDecimal bigDecimal2) {
        return bigDecimal1.subtract(bigDecimal2);
    }

    /**
     * 判断是否为0
     *
     * @param bigDecimal
     * @return
     */
    public static Boolean ifNegative(BigDecimal bigDecimal) {
        return (BigDecimal.ZERO.compareTo(bigDecimal) == 0);
    }

    /**
     * 判断是否为负数
     *
     * @param bigDecimal
     * @return
     */
    public static Boolean ifZero(BigDecimal bigDecimal) {
        return (BigDecimal.ZERO.compareTo(bigDecimal) > 0);
    }


    public static BigDecimal setScale(BigDecimal bigDecimal) {
        return bigDecimal.setScale(ROUND_FLOOR, ROUND_FLOOR);
    }


    public static BigDecimal newObject(Double value) {

        BigDecimal bigDecimal = new BigDecimal(value);

        bigDecimal = bigDecimal.setScale(ROUND_FLOOR, ROUND_FLOOR);
        return bigDecimal;
    }

    /**
     * 去掉小数点后多余的0, 例如 10.00 = 1
     *
     * @param decimal
     * @return
     */
    public static String stripTrailingZeros(BigDecimal decimal) {
        return decimal.stripTrailingZeros().toPlainString();
    }

    public static BigDecimal newObject(Integer value) {

        Double valueDouble = Double.valueOf(value);

        return newObject(valueDouble);
    }

    /**
     * 保留6位小数
     */
    public static Integer SCALE_EIGHT = 8;

    public static Integer SCALE_SIX = 6;

    /**
     * 保留方式 - 截取 ， 参见 BigDecimal.ROUND_FLOOR
     */
    static Integer ROUND_FLOOR = 3;

}
