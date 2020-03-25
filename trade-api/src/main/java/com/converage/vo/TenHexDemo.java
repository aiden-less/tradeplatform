package com.converage.vo;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class TenHexDemo {

    public static void main(String[] args) {
        log.info("==========================================================");
        //定义一个十六进制值
        String strHex3 = "0000000000000000000000000000000000000000000000000000000000989680";
        //将十六进制转化成十进制
        int valueTen2 = Integer.parseInt(strHex3,16);
        log.info(strHex3 + " [十六进制]---->[十进制] " + valueTen2);

        log.info("==========================================================");
        //可以在声明十进制时，自动完成十六进制到十进制的转换
        int valueHex = 0x00001322;
        log.info("int valueHex = 0x00001322 --> " + valueHex);
    }
}
