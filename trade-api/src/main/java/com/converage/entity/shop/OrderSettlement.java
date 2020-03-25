package com.converage.entity.shop;

import lombok.Data;

@Data
public class OrderSettlement {
    private String title;
    private String settlementImg;
    private Integer settlementId;
    private String settlementDesc;

    public OrderSettlement(){}

    OrderSettlement(String title, String settlementImg, Integer settlementId, String settlementDesc) {
        this.title = title;
        this.settlementImg = settlementImg;
        this.settlementId = settlementId;
        this.settlementDesc = settlementDesc;
    }
}
