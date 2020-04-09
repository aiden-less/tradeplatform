package com.converage.entity.market;

import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * Created by 旺旺 on 2020/3/24.
 */
@Data
public class TradePairNews implements Serializable{
    private static final long serialVersionUID = -2908066886828926016L;

    private String tradePairId;
    private String tradeCoinId; //交易币种Id
    private String tradeCoinName; //交易币种名
    private String valuationCoinId; //计价币种Id
    private String valuationCoinName; //计价币种名
    private BigDecimal freshPrice; //最新价
    private double rafRate;//涨跌幅
    private BigDecimal high; //最高价
    private BigDecimal low; //最低价
    private BigDecimal volumn24h;//24小时内成交量
}
