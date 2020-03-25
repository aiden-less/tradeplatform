package com.converage.utils;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MineStringUtils {

    /**
     * 数值类型前面补零（共13位）
     *
     * @param num
     * @return
     */
    public static String supplementZeroGenerateThirteen(int num) {
        String str = String.format("%013d", num);

        return str;
    }

    /**
     * 数值类型前面补零（共16位）
     *
     * @param num
     * @return
     */
    public static String supplementZeroGenerateSixteen(int num) {
        String str = String.format("%016d", num);

        return str;
    }

    /**
     * 数值类型前面补零（共3位）
     *
     * @param num
     * @return
     */
    public static String supplementZeroGenerateThree(int num) {
        String str = String.format("%03d", num);

        return str;
    }

    /**
     * 判断字符串是不是double型
     *
     * @param str
     * @return
     */
    public static boolean isNumeric(String str) {
        Pattern pattern = Pattern.compile("[0-9]+[.]{0,1}[0-9]*[dD]{0,1}");
        Matcher isNum = pattern.matcher(str);
        if (!isNum.matches()) {
            return false;
        }
        return true;
    }

    public static String trim(String str, boolean nullFlag) {
        String tempStr = null;

        if (str != null) {
            tempStr = str.trim();
        }

        if (nullFlag) {
            if ("".equals(tempStr) || "null".equals(tempStr)) {
                tempStr = null;
            }
        } else {
            if (tempStr == null) {
                tempStr = "";
            }
        }

        return tempStr;
    }

    public static String replace(String strSource, String strFrom, String strTo) {
        if (strSource == null) {
            return null;
        }
        int i = 0;
        if ((i = strSource.indexOf(strFrom, i)) >= 0) {
            char[] cSrc = strSource.toCharArray();
            char[] cTo = strTo.toCharArray();
            int len = strFrom.length();
            StringBuffer buf = new StringBuffer(cSrc.length);
            buf.append(cSrc, 0, i).append(cTo);
            i += len;
            int j = i;
            while ((i = strSource.indexOf(strFrom, i)) > 0) {
                buf.append(cSrc, j, i - j).append(cTo);
                i += len;
                j = i;
            }
            buf.append(cSrc, j, cSrc.length - j);
            return buf.toString();
        }
        return strSource;
    }


    public static String deal(String str) {
        str = replace(str, "\\", "\\\\");
        str = replace(str, "'", "\\'");
        str = replace(str, "\r", "\\r");
        str = replace(str, "\n", "\\n");
        str = replace(str, "\"", "\\\"");
        return str;
    }

    public static String GetMapToXML(Map<String, String> param) {
        StringBuffer sb = new StringBuffer();
        sb.append("<xml>");
        for (Map.Entry<String, String> entry : param.entrySet()) {
            sb.append("<" + entry.getKey() + ">");
            sb.append(entry.getValue());
            sb.append("</" + entry.getKey() + ">");
        }
        sb.append("</xml>");
        return sb.toString();
    }

    public static String firstCharToUpperCase(String str) {
        char[] cs = str.toCharArray();
        if (cs[0] >= 97 && cs[0] <= 122) {
            cs[0] -= 32;
        }
        return String.valueOf(cs);
    }


    public static String replaceFirstEndChar(String str, String firstChar, String endChar, String spliceChar) {
        String[] strArr = str.split(spliceChar);
        String resultStr = "";
        for (int i = 0; i < strArr.length; i++) {
            Integer strLength = strArr[i].length();
            StringBuilder sb = new StringBuilder(strArr[i]);
            sb.replace(0, 1, firstChar);
            sb.replace(strLength - 1, strLength, endChar);
            resultStr += sb.toString() + ",";
        }
        return resultStr.substring(resultStr.length() - 1, resultStr.length());
    }

    public static Map<String, String> convertUrlParam2Map(String url) {
        Map<String, String> map = new HashMap<>();
        int endIndex = url.indexOf("?");
        url = url.substring(endIndex + 1, url.length());
        String[] strings = url.split("&");
        for (String s : strings) {
            String[] strings1 = s.split("=");
            map.put(strings1[0], strings1[1]);
        }
        return map;
    }

    /**
     * 将下划线方式命名的字符串转换为驼峰式。如果转换前的下划线大写方式命名的字符串为空，则返回空字符串。<br>
     * 例如：hello_world=》helloWorld
     *
     * @param name 转换前的下划线大写方式命名的字符串
     * @return 转换后的驼峰式命名的字符串
     */
    public static String toCamelCase(CharSequence name) {
        if (null == name) {
            return null;
        }
        String name2 = name.toString();
        if (name2.contains("_")) {
            final StringBuilder sb = new StringBuilder(name2.length());
            boolean upperCase = false;
            for (int i = 0; i < name2.length(); i++) {
                char c = name2.charAt(i);

                if (c == '_') {
                    upperCase = true;
                } else if (upperCase) {
                    sb.append(Character.toUpperCase(c));
                    upperCase = false;
                } else {
                    sb.append(Character.toLowerCase(c));
                }
            }
            return sb.toString();
        } else {
            return name2;
        }
    }
}
