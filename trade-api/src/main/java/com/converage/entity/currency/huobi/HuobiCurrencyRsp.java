package com.converage.entity.currency.huobi;

import lombok.Data;

@Data
public class HuobiCurrencyRsp {
    private Boolean success;
    private Integer status;
    private String message;
    private HuobiCurrencyInfo data;

}
