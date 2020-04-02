package com.converage.constance;

/**
 * Created by 旺旺 on 2020/3/18.
 */
//@Component
public enum RedisKeyEnum {

    TradePair("TradePair"),//交易对
    CctTradePoundageRate("CctTradePoundageRate"),//币币交易手续费比例
    CctKlineList("CctKlineList"),
    CctKlineNewest("CctKlineNewest"),//最新K线数据
    CctTradePairNews("CctTradePairNews"),//最新交易对行情
    CctRafRate("CctRafRate"), //涨跌幅
    CctDoneNumber("CctDoneNumber"), //成交额
    CctHomeTradePairNews("CctHomeTradePairNews");

    private String key;


    RedisKeyEnum(String key) {
        this.key = key;
    }

    public String getKey() {
        return key;
    }


    public String getCctTradePairNews(String valuationCoinId) {

        return this.key + "_" + valuationCoinId;

    }


    public String getCollectTradePairNews(String userId) {
        return this.key + "_" + userId;
    }
}
