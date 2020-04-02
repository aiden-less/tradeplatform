package com.converage.service.quotation;

import com.converage.architecture.dto.Pagination;
import com.converage.architecture.service.BaseService;
import com.converage.client.RedisClient;
import com.converage.constance.RedisKeyEnum;
import com.converage.constance.TransactionEnum;
import com.converage.entity.TradePairNews;

import com.converage.entity.market.CctKline;
import com.converage.entity.market.TradePair;
import com.converage.entity.transaction.CctOrder;
import com.converage.mapper.transaction.CctOrderMapper;
import com.google.common.collect.ImmutableMap;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

/**
 * Created by 旺旺 on 2020/3/19.
 */
@Service
public class QuotationService extends BaseService {

    @Autowired
    private RedisClient redisClient;

    @Autowired
    private CctOrderMapper cctOrderMapper;

    @Autowired
    private MongoTemplate mongoTemplate;

    public void operateCollectQuotation(String userId, String valuationCoinId, String tradeCoinId) {
        String collectTradePairNewsKey = RedisKeyEnum.CctTradePairNews.getCollectTradePairNews(userId);

        String tradePairNewsKey = RedisKeyEnum.CctTradePairNews.getCctTradePairNews(valuationCoinId);
        TradePairNews tradePairNews = (TradePairNews) redisClient.getHashKey(tradePairNewsKey, tradeCoinId);
        String tradePairId = tradePairNews.getTradePairId();

        TradePairNews cache = (TradePairNews) redisClient.getHashKey(collectTradePairNewsKey, tradePairId);
        if (cache != null) {
            redisClient.delete(collectTradePairNewsKey, tradePairId);
        } else {
            redisClient.put(collectTradePairNewsKey, tradePairId, tradePairNews);
        }
    }

    public List<TradePairNews> listCollectQuotation(String userId) {
        return (List<TradePairNews>) redisClient.getHashValues(userId);
    }

    public List<TradePairNews> listQuotation(String valuationCoinId) {
        String redisKey = RedisKeyEnum.CctTradePairNews.getCctTradePairNews(valuationCoinId);
        return (List<TradePairNews>) redisClient.getHashValues(redisKey);
    }

    public Map<String, Object> detail(String tradePairId, String klineType) {
        TradePair tradePair = selectOneById(tradePairId, TradePair.class);
        String valuationCoinId = tradePair.getId();
        String tradeCoinId = tradePair.getTradeCoinId();
        String tradePairNewsKey = RedisKeyEnum.CctTradePairNews.getCctTradePairNews(valuationCoinId);
        TradePairNews tradePairNews = (TradePairNews) redisClient.getHashKey(tradePairNewsKey, tradeCoinId);

        //K线
        Pagination pagination = new Pagination(0, 200);
        Map<String, Object> orderMap = ImmutableMap.of(
                "create_time", "desc"
        );
        List<CctKline> cctKlineList = selectListByWhereString("type=", klineType, pagination, CctKline.class, orderMap);

        //委托买单
        List<CctOrder> cctBuyOrderList = cctOrderMapper.listTradingOrder(tradePairId, TransactionEnum.BUY.getType());
        List<CctOrder> cctSellOrderList = cctOrderMapper.listTradingOrder(tradePairId, TransactionEnum.SELL.getType());

        return ImmutableMap.of(
                "tradePairNews", tradePairNews,
                "cctKlineList", cctKlineList,
                "cctBuyOrderList", cctBuyOrderList,
                "cctSellOrderList", cctSellOrderList
        );
    }

}
