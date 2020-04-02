package com.converage.service.transaction;

import com.converage.architecture.dto.Pagination;
import com.converage.architecture.service.BaseService;
import com.converage.client.RedisClient;
import com.converage.constance.CommonConst;
import com.converage.constance.RedisKeyEnum;
import com.converage.entity.TradePairNews;
import com.converage.entity.information.Article;
import com.converage.entity.market.TradePair;
import com.google.common.collect.ImmutableMap;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by 旺旺 on 2020/3/25.
 */
@Service
public class TradePairService extends BaseService {

    @Autowired
    private RedisClient redisClient;

    public List<TradePairNews> listAdvertiseTradePair() {
        Map<String, Object> whereMap = ImmutableMap.of(
                TradePair.If_Valid + "=", true,
                TradePair.If_Advertise + "=", true
        );

        Map<String, Object> orderMap = ImmutableMap.of(
                TradePair.Advertise_sort, CommonConst.MYSQL_ASC
        );

        List<TradePair> list = selectListByWhereMap(whereMap, new Pagination<>(), TradePair.class, orderMap);

        List<TradePairNews> tradePairNewsList = new ArrayList<>();
        for (TradePair tradePair : list) {
            String tradeCoinId = tradePair.getTradeCoinId();
            String valuationCoinId = tradePair.getValuationCoinId();

            String redisKey = RedisKeyEnum.CctTradePairNews.getCctTradePairNews(valuationCoinId);
            TradePairNews tradePairNews = (TradePairNews) redisClient.getHashKey(redisKey, tradeCoinId);
            tradePairNewsList.add(tradePairNews);

        }

        return tradePairNewsList;
    }

}
