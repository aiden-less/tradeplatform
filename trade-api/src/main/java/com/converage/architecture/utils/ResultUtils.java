package com.converage.architecture.utils;

import com.converage.architecture.dto.Pagination;
import com.converage.architecture.dto.Result;
import com.converage.utils.MineStringUtils;

import java.util.HashMap;
import java.util.Map;

public class ResultUtils {
    public static final Integer RSP_SUCCESS = 0;//normal request
    public static final Integer RSP_FAIL = -1;//error request
    public static final Integer RSP_LOGIN = 1;//re-login
    public static final Integer MINING = 2;//mining


    public static final Integer RSP_USER_NONE_SETTLE_INVITE_CODE = -999;//用户设置邀请码
    public static final Integer RSP_USER_VOUCHER_RECORD_NONE = -1000;//用户没有兑换券


    public static final String WECHATPAY_RETURN_CODE_FIELD = "return_code";
    public static final String WECHATPAY_RETURN_MSG_FIELD = "return_msg";
    public static final String WECHATPAY_RETURN_SUCCESS_CODE = "SUCCESS";
    public static final String WECHATPAY_RETURN_FAIL_CODE = "FAIL";

    //request success
    public static Result success() {
        Result result = new Result();
        result.setStateCode(RSP_SUCCESS);
        return result;
    }

    //request success
    public static Result success(Object data, String msg) {
        Result result = new Result();
        result.setStateCode(RSP_SUCCESS);
        result.setMessage(msg);
        result.setData(data);
        return result;
    }

    //request success
    public static Result success(Object data, Integer count) {
        Result result = new Result();
        result.setStateCode(RSP_SUCCESS);
        result.setCount(count);
        result.setData(data);
        return result;
    }


    public static Result<?> success(Object data, Pagination pagination) {
        Result result = new Result();
        Integer count = pagination == null ? 0 : pagination.getTotalRecordNumber();
        result.setStateCode(RSP_SUCCESS);
        result.setCount(count);
        result.setData(data);
        return result;
    }

    //request success
    public static Result success(Object data) {
        Result result = new Result();
        result.setStateCode(RSP_SUCCESS);
        result.setData(data);
        return result;
    }

    //request success
    public static Result success(String msg) {
        Result result = new Result();
        result.setStateCode(RSP_SUCCESS);
        result.setMessage(msg);
        return result;
    }

    //request fail
    public static Result error(int code, String msg) {
        Result result = new Result();
        result.setStateCode(code);
        result.setMessage(msg);
        return result;
    }

    //request fail
    public static Result error(String msg) {
        Result result = new Result();
        result.setStateCode(RSP_FAIL);
        result.setMessage(msg);
        return result;
    }

    public static Result<?> error(Integer rspUserVoucherRecordNone, Map<String, Object> map, String message) {
        Result result = new Result();
        result.setStateCode(rspUserVoucherRecordNone);
        result.setData(map);
        result.setMessage(message);
        return result;

    }

    //request fail
    public static Result reLogin(String msg) {
        Result result = new Result();
        result.setStateCode(RSP_LOGIN);
        result.setMessage(msg);
        return result;
    }

    //weChatPay success
    public static Object weChatPaySuccess(Map<String, String> returnMap) {
        returnMap.put(WECHATPAY_RETURN_CODE_FIELD, WECHATPAY_RETURN_SUCCESS_CODE);
        returnMap.put(WECHATPAY_RETURN_MSG_FIELD, "OK");
        return MineStringUtils.GetMapToXML(returnMap);
    }

    //weChatPay success
    public static String weChatPaySuccess() {
        Map<String, String> returnMap = new HashMap<>();
        returnMap.put(WECHATPAY_RETURN_CODE_FIELD, WECHATPAY_RETURN_SUCCESS_CODE);
        returnMap.put(WECHATPAY_RETURN_MSG_FIELD, "OK");
        return MineStringUtils.GetMapToXML(returnMap);
    }

    //weChatPay fail
    public static String weChatPayFail() {
        Map<String, String> returnMap = new HashMap<>();
        returnMap.put(WECHATPAY_RETURN_CODE_FIELD, WECHATPAY_RETURN_FAIL_CODE);
        returnMap.put(WECHATPAY_RETURN_MSG_FIELD, "error");
        return MineStringUtils.GetMapToXML(returnMap);
    }


    //aliPay success
    public static String aliPaySuccess(){
        return "success";
    }

    public static String aliPayFail(){
        return "failure";
    }



}
