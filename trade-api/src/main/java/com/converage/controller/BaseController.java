package com.converage.controller;

import com.converage.architecture.dto.Result;
import com.converage.architecture.exception.BusinessException;
import com.converage.architecture.exception.LoginException;
import com.converage.architecture.utils.ResultUtils;
import com.converage.exception.AliPayNotifyException;
import com.converage.exception.UserVoucherNoneException;
import com.converage.exception.WeChatPayNotifyException;
import com.google.common.collect.ImmutableMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.validation.BindException;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Map;

@RestControllerAdvice
public class BaseController {

    private final static Logger logger = LoggerFactory.getLogger(BaseController.class);

    @ExceptionHandler(Exception.class)
    public Result<?> exceptionHandler(Exception e) {
        logger.error(getExceptionInfo(e));
        String[] arr = e.getClass().getName().split("\\.");
//        Result<?> result = ResultUtils.error(ResultUtils.RSP_FAIL, arr[arr.length - 1]);
        return ResultUtils.error(ResultUtils.RSP_FAIL, "SYSTEM ERROR CODE[100[");
    }


    @ExceptionHandler(BusinessException.class)
    public Result<?> businessExceptionHandler(BusinessException e) {
        String location = null;
        StackTraceElement[] stackTrace = e.getStackTrace();
        if (stackTrace != null && stackTrace.length > 0) {
            location = stackTrace[1].toString();
        }
        String message = e.getMessage();
        logger.warn("message: {}, location:{}", message, location);
        return ResultUtils.error(ResultUtils.RSP_FAIL, message);
    }


    @ExceptionHandler(UserVoucherNoneException.class)
    public Result<?> userVoucherNoneExceptionHandler(UserVoucherNoneException e) {
        logger.error(getExceptionInfo(e));
        Map<String, Object> map = ImmutableMap.of("voucherBuyUrl", "http://www.baidu.com");
        return ResultUtils.error(ResultUtils.RSP_USER_VOUCHER_RECORD_NONE, map, e.getMessage());
    }

    @ExceptionHandler(WeChatPayNotifyException.class)
    public String weChatPayNotifyExceptionHandler(WeChatPayNotifyException e) {
        logger.error(getExceptionInfo(e));
        return ResultUtils.weChatPayFail();
    }

    @ExceptionHandler(AliPayNotifyException.class)
    public String aliPayNotifyExceptionHandler(AliPayNotifyException e) {
        logger.error(getExceptionInfo(e));
        return ResultUtils.aliPayFail();
    }

    @ExceptionHandler(LoginException.class)
    public Result<?> loginExceptionHandler(Exception e) {
        return ResultUtils.reLogin("");
    }

    @ExceptionHandler(BindException.class)
    public Result<?> BindExceptionHandler(Exception e) {
        String message = "缺少必要参数";
        ObjectError objectError = ((BindException) e).getAllErrors().get(0);
        if (objectError != null) {
            message = objectError.getDefaultMessage();
        }
        return ResultUtils.error(message);
    }

    private static String getExceptionInfo(Throwable e) {
        StringWriter stringWriter = new StringWriter();
        PrintWriter printWriter = new PrintWriter(stringWriter, true);
        e.printStackTrace(printWriter);
        printWriter.flush();
        stringWriter.flush();
        return stringWriter.toString();
    }
}
