package com.converage.service;

import com.converage.constance.RedisKeyEnum;
import com.converage.entity.TradePairNews;
import com.converage.middleware.redis.RedisClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Created by 旺旺 on 2020/3/19.
 */
@Service
public class QuotationService {

    @Autowired
    private RedisClient redisClient;


    public void operateCollectQuotation(String userId, TradePairNews tradePairNews) {
        String redisKey = RedisKeyEnum.CctTradePairNews.getCollectTradePairNews(userId);
        redisClient.put(redisKey, tradePairNews.getTradePairId(), tradePairNews);
    }

    public List<TradePairNews> listCollectQuotation(String userId) {
        return (List<TradePairNews>) redisClient.getHashValues(userId);
    }

    public List<TradePairNews> listQuotation(String valuationCoinId) {
        String redisKey = RedisKeyEnum.CctTradePairNews.getCctTradePairNews(valuationCoinId);
        return (List<TradePairNews>) redisClient.getHashValues(redisKey);

    }

}
