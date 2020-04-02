package com.converage.service;


import com.converage.constance.KlineTypeEnum;
import com.converage.constance.RedisKeyEnum;
import com.converage.entity.CctKline;
import com.converage.entity.CctRafRate;
import com.converage.entity.TradePair;
import com.converage.middleware.redis.RedisClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Service;
import com.converage.entity.TradePairNews;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.util.Objects;
import java.util.UUID;

/**
 * Created by 旺旺 on 2020/3/19.
 */
@Service
public class CctKlineService {

    @Autowired
    private RedisKeyService redisKeyService;

    @Autowired
    private RedisClient redisClient;

    @Autowired
    private MongoTemplate mongoTemplate;

    //定时任务，持久化K线到mongoDb
    public void saveKline(String tradePairName) {
        LocalDateTime now = LocalDateTime.now();

        String enumKey = RedisKeyEnum.CctKlineNewest.getKey();

        for (int i = 0; i < KlineTypeEnum.values().length; i++) {
            KlineTypeEnum enumEnum = KlineTypeEnum.values()[i];

            String type = enumEnum.getType();
            String redisKey = redisKeyService.getKlineKey(enumKey, tradePairName);
            CctKline cctKline = (CctKline) redisClient.getHashKey(redisKey, type);

            CctKline cctKlineSave = new CctKline();
            BigDecimal freshPrice = cctKlineSave.getFresh();

            cctKlineSave.setId(UUID.randomUUID().toString().replace("-", ""));
            cctKlineSave.setType(enumEnum.getType());
            cctKlineSave.setOpen(cctKline.getClose());
            cctKlineSave.setClose(cctKline.getFresh());
            cctKlineSave.setHigh(cctKline.getHigh());
            cctKlineSave.setLow(cctKline.getLow());
            cctKlineSave.setTradeCoinVolume(cctKline.getTradeCoinVolume());
            cctKlineSave.setValuationCoinVolume(cctKline.getValuationCoinVolume());
            cctKlineSave.setCreateTime(new Timestamp(System.currentTimeMillis()));

            switch (enumEnum) {
                //新增一分线 定时任务每分钟执行一遍 直接新增
                case OneMinute:
                    mongoTemplate.save(cctKlineSave);
                    break;

                //新增五分钟线
                case FiveMinute:
                    if (now.getMinute() % 5 == 0) {
                        mongoTemplate.save(cctKlineSave);
                    }
                    break;

                //新增十五分钟线
                case FifteenMinute:
                    if (now.getMinute() % 15 == 0) {
                        mongoTemplate.save(cctKlineSave);
                    }
                    break;

                //新增三十分钟线
                case ThirtyMinute:
                    if (now.getMinute() % 30 == 0) {
                        mongoTemplate.save(cctKlineSave);
                    }
                    break;

                //新增一小时线
                case OneHour:
                    if (now.getMinute() == 0) {
                        mongoTemplate.save(cctKlineSave);
                    }
                    break;

                //新增四小时线
                case FourHours:
                    if (now.getHour() % 4 == 0) {
                        mongoTemplate.save(cctKlineSave);
                    }
                    break;

                //新增一天线
                case OneDay:
                    if (now.getHour() == 0) {
                        mongoTemplate.save(cctKlineSave);
                    }
                    break;

                //新增一周线
                case OneWeek:
                    if (now.getDayOfWeek() == DayOfWeek.SUNDAY) {
                        mongoTemplate.save(cctKlineSave);
                    }
                    break;

                //新增一月线
                case OneMonth:
                    if (now.getDayOfMonth() == 1) {
                        mongoTemplate.save(cctKlineSave);
                    }
                    break;

            }

            //重置最新K线的交易量
            cctKline.setTradeCoinVolume(BigDecimal.ZERO);
            cctKline.setHigh(freshPrice);
            cctKline.setLow(freshPrice);
            redisClient.put(redisKey, type, cctKline);

        }
    }


    //更新最新的K线缓存最高最低价，成交量
    public void updateKline(TradePair tradePair, BigDecimal doneUnit, BigDecimal doneNumber) {
        String enumKey = RedisKeyEnum.CctKlineNewest.getKey();


        String tradePairName = tradePair.getPairName();
        String tradePairId = tradePair.getId();
        String tradeCoinName = tradePair.getTradeCoinName();
        String valuationCoinName = tradePair.getValuationCoinName();
        String valuationCoinId = tradePair.getValuationCoinId();

        for (int i = 0; i < KlineTypeEnum.values().length; i++) {
            KlineTypeEnum enumEnum = KlineTypeEnum.values()[i];

            String type = enumEnum.getType();
            String redisKey = redisKeyService.getKlineKey(enumKey, tradePairName);
            CctKline cctKline = (CctKline) redisClient.getHashKey(redisKey, type);

            //更新最高价
            if (doneUnit.compareTo(cctKline.getHigh()) > 0) {
                cctKline.setHigh(doneUnit);
            }

            //更新最低价
            if (doneUnit.compareTo(cctKline.getLow()) < 0) {
                cctKline.setLow(doneUnit);
            }

            //增加交易币种成交量
            cctKline.setTradeCoinVolume(cctKline.getTradeCoinVolume().add(doneNumber));
            //增加计价币种成交量
            cctKline.setValuationCoinVolume(cctKline.getValuationCoinVolume().add(doneUnit.multiply(doneNumber)));

            //更新最新价
            cctKline.setFresh(doneUnit);

            redisClient.put(redisKey, type, cctKline);

            BigDecimal open = cctKline.getOpen();
            double rafRate = (doneUnit.subtract(open)).divide(open, 2, BigDecimal.ROUND_DOWN).multiply(BigDecimal.valueOf(100)).doubleValue();
            if (Objects.equals(type, KlineTypeEnum.OneMinute.getType())) {
                //更新涨跌幅
                CctRafRate cctRafRate = new CctRafRate(tradePairId, doneUnit, tradeCoinName, valuationCoinName, rafRate);
                redisClient.add(RedisKeyEnum.CctRafRate.getKey(), cctRafRate, rafRate);

                //更新交易对行情
                String tradePairNewsKey = RedisKeyEnum.CctTradePairNews.getCctTradePairNews(valuationCoinId);
                TradePairNews tradePairNews = (TradePairNews) redisClient.getHashKey(tradePairNewsKey, tradeCoinName);
                tradePairNews.setFreshPrice(doneUnit);
                tradePairNews.setRafRate(rafRate);
                tradePairNews.setHigh(cctKline.getHigh());
                tradePairNews.setLow(cctKline.getLow());
                redisClient.put(tradePairNewsKey, valuationCoinName, tradePairNews);
            }
        }
    }

}
