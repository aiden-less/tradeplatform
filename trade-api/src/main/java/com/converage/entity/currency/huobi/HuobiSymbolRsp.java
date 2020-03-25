package com.converage.entity.currency.huobi;

import lombok.Data;

import java.util.List;

@Data
public class HuobiSymbolRsp extends HuobiApiRsp {
    private List<HuobiCommonSymbol> data;
}
