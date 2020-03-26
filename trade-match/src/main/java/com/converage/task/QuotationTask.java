package com.converage.task;


import com.converage.constance.RedisKeyEnum;
import com.converage.entity.TradePair;
import com.converage.middleware.redis.RedisClient;
import com.converage.service.CctKlineService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Created by 旺旺 on 2020/3/19.
 */
@Component
public class QuotationTask {

    @Autowired
    private CctKlineService cctKlineService;

    @Autowired
    private RedisClient redisClient;


    //保存K线定时任务
    @Scheduled(cron = "0 */1 * * * ?")
    public void saveKline() {
        List<TradePair> tradePairList = (List<TradePair>) redisClient.getHashValues(RedisKeyEnum.TradePair.getKey());

        tradePairList.parallelStream().forEach(tradePair -> {
            String pairName = tradePair.getPairName();
            cctKlineService.saveKline(pairName);
        });
    }


    //更新交易对的交易额度
    @Scheduled(cron = "0 */1 * * * ?")
    public void updateCctVolume() {
        //按交易对名分组查询


    }
}
