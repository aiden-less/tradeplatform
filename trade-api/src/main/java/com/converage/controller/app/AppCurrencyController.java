package com.converage.controller.app;

import com.converage.architecture.dto.Result;
import com.converage.architecture.exception.BusinessException;
import com.converage.architecture.utils.JwtUtils;
import com.converage.architecture.utils.ResultUtils;
import com.converage.entity.currency.coingecko.CurrencyInfoTab;
import com.converage.service.currency.CurrencyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@RestController
@RequestMapping("app/currency")
public class AppCurrencyController {

    @Autowired
    private CurrencyService currencyService;

    /**
     * 行情选项列表
     */
    @RequestMapping("tabs")
    public Result<?> tabs() {
        List<CurrencyInfoTab> currencyInfoTabList = Arrays.asList(
                new CurrencyInfoTab("添加自选", 1),
                new CurrencyInfoTab("Coingecko", 2)
        );
        return ResultUtils.success(currencyInfoTabList);
    }


    /**
     * 所有货币行情
     *
     * @param pageNum
     * @param pageSize
     * @return
     */
    @RequestMapping("allCoinInfo")
    public Result<?> allCoinInfo(HttpServletRequest request, Integer tabValue, Integer pageNum, Integer pageSize, Integer sortColumn, Integer sortType) {
        String userId = JwtUtils.getUserByToken(request.getHeader(JwtUtils.ACCESS_TOKEN_NAME)).getId();
        switch (tabValue) {
            case 1:
                return ResultUtils.success(currencyService.collectCoinInfoList(userId));
            case 2:
                return ResultUtils.success(currencyService.allCoinInfo(pageNum, pageSize, sortColumn, sortType));
            default:
                throw new BusinessException("选项值有误");
        }
    }

    /**
     * 自选列表
     *
     * @param request
     * @return
     */
    @RequestMapping("collectCoinInfo")
    public Result<?> collectCoinInfo(HttpServletRequest request) {
        String userId = JwtUtils.getUserByToken(request.getHeader(JwtUtils.ACCESS_TOKEN_NAME)).getId();
        return ResultUtils.success(currencyService.collectCoinInfoList(userId));
    }

//    /**
//     * 货币详情
//     *
//     * @return
//     */
//    @RequestMapping("coinInfo")
//    public Result<?> coinInfo(HttpServletRequest request, String symbol) {
//        String userId = JwtUtils.getUserByToken(request.getHeader(JwtUtils.ACCESS_TOKEN_NAME)).getId();
//        return ResultUtils.success(currencyService.coinInfo(userId, symbol));
//    }

    /**
     * 添加/移除货币
     *
     * @param request
     * @param symbol
     * @return
     */
    @RequestMapping("collect")
    public Result<?> collect(HttpServletRequest request, String symbol) {
        String userId = JwtUtils.getUserByToken(request.getHeader(JwtUtils.ACCESS_TOKEN_NAME)).getId();
        currencyService.collectCoinInfo(userId, symbol);
        return ResultUtils.success();
    }
}
