package com.converage.controller.app;

import com.alibaba.fastjson.JSONObject;
import com.converage.architecture.dto.Pagination;
import com.converage.architecture.dto.Result;
import com.converage.architecture.utils.JwtUtils;
import com.converage.architecture.utils.ResultUtils;
import com.converage.controller.app.req.TradeOrderPayReq;
import com.converage.service.common.RSAService;
import com.converage.service.transaction.CctService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

import static com.converage.constance.CommonConst.REQUST_TIME_OUT_SECOND;

/**
 * Created by 旺旺 on 2020/3/20.
 */
@RestController
@RequestMapping("app/cct")
public class AppCctController {

    @Autowired
    private CctService cctService;

    @Autowired
    private RSAService rsaService;


    //创建挂单
    @RequestMapping("order/create")
    public Result<?> transactionTrade(HttpServletRequest request, String desKeyStr, String paramStr) throws Exception {
        String userId = JwtUtils.getUserByToken(request.getHeader(JwtUtils.ACCESS_TOKEN_NAME)).getId();

        TradeOrderPayReq tradeOrderPayReq = JSONObject.parseObject(rsaService.decryptParam(desKeyStr, paramStr), TradeOrderPayReq.class);
        rsaService.checkRequestTimeout(tradeOrderPayReq, REQUST_TIME_OUT_SECOND);

        cctService.createTransactionOrder(
                userId, tradeOrderPayReq.getTransactionType(), tradeOrderPayReq.getTransactionUnit(), tradeOrderPayReq.getTransactionNumber(), tradeOrderPayReq.getPayPassword()
        );

        return ResultUtils.success("交易成功");
    }

    //取消订单
    @RequestMapping("order/cancel")
    public Result<?> transactionOrderCancel(HttpServletRequest request, String recordId) {
        return ResultUtils.success("取消成功");
    }

    //用户订单
    @RequestMapping("order/owner")
    public Result<?> transactionOrderInfo(HttpServletRequest request, Integer transactionType, Integer status, Pagination pagination) {
        String userId = JwtUtils.getUserByToken(request.getHeader(JwtUtils.ACCESS_TOKEN_NAME)).getId();
        return ResultUtils.success();
    }

    //所有订单
    @RequestMapping("order/all")
    public Result<?> transactionAllOrderInfo(HttpServletRequest request, Integer transactionType, Integer status, Pagination pagination) {
        String userId = JwtUtils.getUserByToken(request.getHeader(JwtUtils.ACCESS_TOKEN_NAME)).getId();
        return ResultUtils.success();
    }

}
