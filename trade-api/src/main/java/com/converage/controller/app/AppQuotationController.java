package com.converage.controller.app;

import com.converage.architecture.dto.Result;
import com.converage.architecture.utils.JwtUtils;
import com.converage.architecture.utils.ResultUtils;
import com.converage.service.quotation.QuotationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

/**
 * Created by 旺旺 on 2020/3/24. 行情controller
 */
@RequestMapping(value = "/app/quotation")
@RestController
public class AppQuotationController {

    @Autowired
    private QuotationService quotationService;


    //收藏/取消行情
    @RequestMapping("collect")
    public Result<?> collectQuotation(HttpServletRequest request, String valuationCoinName, String tradeCoinName) {
        String userId = JwtUtils.getUserByToken(request.getHeader(JwtUtils.ACCESS_TOKEN_NAME)).getId();
        quotationService.operateCollectQuotation(userId, valuationCoinName, tradeCoinName);
        return ResultUtils.success();
    }

    //个人收藏行情
    @RequestMapping("collect/list")
    public Result<?> listCollectQuotation(HttpServletRequest request) {
        String userId = JwtUtils.getUserByToken(request.getHeader(JwtUtils.ACCESS_TOKEN_NAME)).getId();
        return ResultUtils.success(quotationService.listCollectQuotation(userId));
    }

    //所有行情
    @RequestMapping("list")
    public Result<?> listQuotation(String valuationCoinId) {
        return ResultUtils.success(quotationService.listQuotation(valuationCoinId));
    }

    //所有行情
    @RequestMapping("detail")
    public Result<?> detail(String tradePairId, String klineType) {
        return ResultUtils.success(quotationService.detail(tradePairId, klineType));
    }


}
