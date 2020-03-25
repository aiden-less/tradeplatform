package com.converage.entity.currency.huobi;

import lombok.Data;

import java.util.List;

@Data
public class HuobiMarketRsp extends HuobiApiRsp{
    private List<HuobiMarket> data;
}
