package com.converage.service.currency;

import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.ImmutableMap;
import com.converage.architecture.dto.Pagination;
import com.converage.architecture.service.BaseService;
import com.converage.client.RedisClient;
import com.converage.constance.RedisKeyConst;
import com.converage.entity.currency.*;
import com.converage.entity.currency.coingecko.CoingeckoCurrencyApiEntity;
import com.converage.entity.currency.coingecko.CoingeckoCurrencyInfo;
import com.converage.entity.currency.huobi.HuobiCurrencyInfo;
import com.converage.entity.currency.huobi.HuobiCurrencyRsp;
import com.converage.mapper.currency.CoingeckoCurrencyMapper;
import com.converage.mapper.currency.HuobiCurrencyMapper;
import com.converage.utils.HttpClientUtils;
import com.converage.utils.RegxpUtils;
import com.converage.utils.ValueCheckUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallbackWithoutResult;
import org.springframework.transaction.support.TransactionTemplate;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class CurrencyService extends BaseService {

    @Autowired
    private RedisClient redisClient;

    @Autowired
    private CoingeckoCurrencyMapper coingeckoCurrencyMapper;

    @Autowired
    private HuobiCurrencyMapper huobiCurrencyMapper;

    @Autowired
    private TransactionTemplate transactionTemplate;

    /**
     * 更新货币行情缓存
     */
    public void updateCoinInfo() {
        //获取人民币汇率
        String url1 = "https://cdn.jsdelivr.net/gh/prebid/currency-file@1/latest.json";
        String result1 = HttpClientUtils.doGet(url1, new HashMap<>(), HttpClientUtils.CHARSET);
        ExchangeRate exchangeRate = JSONObject.parseObject(result1, ExchangeRate.class);
        BigDecimal CNYToUsdRate = exchangeRate.getConversions().get("USD").get("CNY");
        transactionTemplate.execute(new TransactionCallbackWithoutResult() {
            @Override
            protected void doInTransactionWithoutResult(TransactionStatus transactionStatus) {
                Integer i = 1;
                while (true) {
                    //获取数字货币详情
                    String url = "https://api.coingecko.com/api/v3/coins/markets";
                    Map<String, String> paramMap = ImmutableMap.of(
                            "vs_currency", "cny",
                            "order", "market_cap_desc",
                            "page", i.toString(),
                            "per_page", "250",
                            "price_change_percentage", "1h"
                    );
                    String result = HttpClientUtils.doGet(url, paramMap, HttpClientUtils.CHARSET);
                    List<CoingeckoCurrencyApiEntity> resultList = JSONObject.parseArray(result, CoingeckoCurrencyApiEntity.class);
                    if (resultList == null || resultList.size() == 0) {
                        break;
                    }

                    for (CoingeckoCurrencyApiEntity currencyApiInfo : resultList) {
                        CoingeckoCurrencyInfo currencyInfo = new CoingeckoCurrencyInfo(currencyApiInfo);
                        currencyInfo.setUSDPrice(CNYToUsdRate);
//                        Integer count = coingeckoCurrencyMapper.selectCountByCoinId();

                        CoingeckoCurrencyInfo currencyInfoPo = selectOneByWhereString(CoingeckoCurrencyInfo.Coin_id + "=", currencyInfo.getCoinId(), CoingeckoCurrencyInfo.class);
                        if (currencyInfoPo == null) {
                            insert(currencyInfo);
                        } else {
                            update(currencyInfoPo);
                        }
                    }
                    i++;
                }
            }
        });

    }


    /**
     * 查询所有货币信息
     *
     * @param pageNum
     * @param pageSize
     * @return
     */
    public List<HuobiCurrencyInfo> allCoinInfo(Integer pageNum, Integer pageSize, Integer sortColumn, Integer sortType) {
        if (pageNum == null) {
            pageNum = 1;
        }
        if (pageSize == null) {
            pageSize = 50;
        }

        Pagination pagination = new Pagination(pageNum, pageSize);

        System.out.println(1);
        String symbol = "usdt";
        return huobiCurrencyMapper.listCurrencyInfoBySymbol(symbol,pagination);
    }


    /**
     * 按货币简称搜索/货币详情
     *
     * @param symbol
     * @return
     */
    public CoingeckoCurrencyInfo coinInfo(String userId, String symbol) {
        ValueCheckUtils.notEmpty(symbol, "请输入货币简称");
        symbol = symbol.toLowerCase();

        Map<String, String> whereMap = ImmutableMap.of(
                "r", "h7ccnrrma2p",
                "lang", "zh-cn",
                "currency", symbol
        );

        Map<String, Object> infoWhereMap = ImmutableMap.of(
                UserCollectCoinInfo.User_id + "=", userId,
                UserCollectCoinInfo.Coin_symbol + "=", symbol
        );


        String url = "https://www.huobi.co/-/x/hb/p/api/contents/pro/currency_introduction";
        String result = HttpClientUtils.doGet(url, whereMap, HttpClientUtils.CHARSET);
        HuobiCurrencyRsp entity = JSONObject.parseObject(result, HuobiCurrencyRsp.class);
        HuobiCurrencyInfo huobiCurrencyInfo = entity.getData();

        CoingeckoCurrencyInfo coingeckoCurrencyInfo = selectOneByWhereString(CoingeckoCurrencyInfo.Symbol + "=", symbol, CoingeckoCurrencyInfo.class);

        if (huobiCurrencyInfo.getFullName() != null && coingeckoCurrencyInfo != null) {
            coingeckoCurrencyInfo.setIntroduction(huobiCurrencyInfo.getSummary());
            coingeckoCurrencyInfo.setPublishTime(huobiCurrencyInfo.getPublishTime());
            coingeckoCurrencyInfo.setFullName(huobiCurrencyInfo.getFullName());

            String regxp = "<([^>]*)>";
            coingeckoCurrencyInfo.setWhitePaper(RegxpUtils.regxpString(huobiCurrencyInfo.getWhitePaper(), regxp));
            coingeckoCurrencyInfo.setBlockQuery(RegxpUtils.regxpString(huobiCurrencyInfo.getBlockQuery(), regxp));
            coingeckoCurrencyInfo.setOfficialWebsite(RegxpUtils.regxpString(huobiCurrencyInfo.getOfficialWebsite(), regxp));

            UserCollectCoinInfo userCollectCoinInfo = selectOneByWhereMap(infoWhereMap, UserCollectCoinInfo.class);

            coingeckoCurrencyInfo.setIfCollect(userCollectCoinInfo != null);
        }


        return coingeckoCurrencyInfo;
    }

    /**
     * 自选货币信息
     *
     * @param userId
     * @return
     */
    public List<CoingeckoCurrencyInfo> collectCoinInfoList(String userId) {
        List<UserCollectCoinInfo> userCollectCoinInfos = selectListByWhereString(UserCollectCoinInfo.User_id + "=", userId, UserCollectCoinInfo.class);

        if (userCollectCoinInfos.size() == 0) {
            return new ArrayList<>();
        }

        List<String> coinIds = new ArrayList<>();
        for (UserCollectCoinInfo userCollectCoinInfo : userCollectCoinInfos) {
            String symbol = userCollectCoinInfo.getCoinSymbol();
            coinIds.add(symbol);
        }


        return coingeckoCurrencyMapper.listUserCollect(coinIds);
    }

    /**
     * 添加/删除收藏货币
     */
    public void collectCoinInfo(String userId, String symbol) {
        ValueCheckUtils.notEmpty(symbol, "请输入货币简称");
        Map<String, Object> whereMap = ImmutableMap.of(
                UserCollectCoinInfo.User_id + "=", userId,
                UserCollectCoinInfo.Coin_symbol + "=", symbol
        );
        UserCollectCoinInfo userCollectCoinInfo = selectOneByWhereMap(whereMap, UserCollectCoinInfo.class);
        if (userCollectCoinInfo != null) {
            ValueCheckUtils.notZero(delete(userCollectCoinInfo), "取消收藏失败");
        } else {
            UserCollectCoinInfo userCollectCoinInfo1 = new UserCollectCoinInfo(userId, symbol);
            ValueCheckUtils.notZero(insert(userCollectCoinInfo1), "收藏失败");
        }

    }
}
