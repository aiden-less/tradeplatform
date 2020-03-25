package com.converage.entity.currency.coingecko;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class CoingeckoCurrencyApiEntity {//币虎币种信息实体
    private String id;
    private String symbol;
    private String name;
    private String image;
    private BigDecimal current_price;
    private BigDecimal market_cap;
    private Integer market_cap_rank;
    private BigDecimal total_volume;
    private BigDecimal high_24h;
    private BigDecimal low_24h;
    private BigDecimal price_change_24h;
    private BigDecimal price_change_percentage_24h;
    private BigDecimal market_cap_change_24h;
    private BigDecimal market_cap_change_percentage_24h;
    private BigDecimal circulating_supply;
    private BigDecimal total_supply;
    private BigDecimal ath;
    private BigDecimal ath_change_percentage;
    private String ath_date;
}
