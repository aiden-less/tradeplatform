package com.converage.utils;

import net.sf.json.JSONObject;
import org.apache.http.HttpResponse;
import org.apache.http.util.EntityUtils;

import java.util.HashMap;
import java.util.Map;

public class AliyunInterface {
    public static String IDCARD_VERTIFY_API_CODE = "c967fa109a9d44559066fccac9bc68e0";
    public static String IDCARD_VERTIFY_URL = "http://idcert.market.alicloudapi.com/idcard";
//    public static String IDCARD_VERTIFY_URL  = "http://1.api.apistore.cn/idcard3";

    public static String idCardVertify(String realName, String licenseNumber, String Method) {
        String host = "https://idcert.market.alicloudapi.com";
        String path = "/idcard";
        String method = "GET";
        String appcode = "c967fa109a9d44559066fccac9bc68e0";
        Map<String, String> headers = new HashMap<>();
        //最后在header中的格式(中间是英文空格)为Authorization:APPCODE 83359fd73fe94948385f570e3c139105
        headers.put("Authorization", "APPCODE " + appcode);
        Map<String, String> querys = new HashMap<>();
        querys.put("name", realName);
        querys.put("idCard", licenseNumber);
        //JDK 1.8示例代码请在这里下载：  http://code.fegine.com/java/cmapi022049.zip


        String resultStr = "";
        try {

            HttpResponse response = HttpUtils.doGet(host, path, method, headers, querys);
            //System.out.println(response.toString());如不输出json, 请打开这行代码，打印调试头部状态码。
            //状态码: 200 正常；400 URL无效；401 appCode错误； 403 次数用完； 500 API网管错误
            //获取response的body
            resultStr = EntityUtils.toString(response.getEntity());
        } catch (Exception e) {
            e.printStackTrace();
        }

        return resultStr;
    }

    // 发起请求,获取内容
    public static void main(String[] args) {
        String name = "邓圣彬";
        String cardNo = "44070219941006153X";

        //发送请求
        String result = idCardVertify(name, cardNo, "GET");
        //输出结果
        System.out.println(result);
        //JSON
        JSONObject object = JSONObject.fromObject(result);
        //输出状态码
        System.out.println(object.getString("status"));
        //输出返回结果
        System.out.println(object.get("msg"));
    }
}
