package com.converage.entity.currency.coingecko;

import lombok.Data;

@Data
public class CurrencyInfoTab {
    private String name;
    private Integer value;

    public CurrencyInfoTab(){}

    public CurrencyInfoTab(String name, Integer value) {
        this.name = name;
        this.value = value;
    }
}
