package com.converage.entity.currency.huobi;

import lombok.Data;

@Data
public class HuobiCommonSymbol {
    private String base_currency;
    private String quote_currency;
    private String price_precision;
    private String amount_precision;
    private String symbol_partition;
    private String symbol;
}
