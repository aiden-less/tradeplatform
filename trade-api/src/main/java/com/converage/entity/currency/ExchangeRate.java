package com.converage.entity.currency;

import lombok.Data;

import java.math.BigDecimal;
import java.util.Map;

@Data
public class ExchangeRate {
    public String dataAsOf;
    public Map<String,Map<String,BigDecimal>> conversions;
}
