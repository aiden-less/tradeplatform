package com.converage.entity.currency.huobi;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class HuobiMarket {
    private BigDecimal open;
    private BigDecimal close;
    private BigDecimal low;
    private BigDecimal high;
    private BigDecimal amount;
    private BigDecimal count;
    private BigDecimal vol;
    private String symbol;
    private BigDecimal changeRate;
}
