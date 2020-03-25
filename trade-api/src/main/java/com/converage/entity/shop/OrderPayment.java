package com.converage.entity.shop;

import lombok.Data;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@Data
public class OrderPayment {
    private BigDecimal price;
    private String settlementName;
    private String orderName;
    private List<Map<String, Object>> settlementList;
    private String buyStr;
    private Boolean buyFlag;
}
