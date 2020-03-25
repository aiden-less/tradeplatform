package com.converage.entity.shop;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class GoodsTempInfo {
    String shopId;
    String spuId;
    Boolean ifSku;
    String skuOrSpuId;
    String goodsDescription;
    BigDecimal currencyPrice;
    BigDecimal integralPrice;
    BigDecimal cnyPrice;
    BigDecimal currencyReward;


    public GoodsTempInfo() {
    }

    public GoodsTempInfo(String skuOrSpuId, BigDecimal currencyPrice, BigDecimal integralPrice, BigDecimal cnyPrice, BigDecimal currencyReward, GoodsSpu goodsSpu) {
        this.shopId = goodsSpu.getShopId();
        this.spuId = goodsSpu.getId();
        this.ifSku = goodsSpu.getIfSku();
        this.skuOrSpuId = skuOrSpuId;
        this.currencyPrice = currencyPrice;
        this.integralPrice = integralPrice;
        this.cnyPrice = cnyPrice;
        this.currencyReward = currencyReward;
        this.goodsDescription = goodsSpu.getGoodsDescription();
    }
}
