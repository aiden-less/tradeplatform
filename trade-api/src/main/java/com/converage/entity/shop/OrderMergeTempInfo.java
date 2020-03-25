package com.converage.entity.shop;

import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
public class OrderMergeTempInfo {
    private List<String> orderIdList;
    private String orderTargetId;
    private BigDecimal orderCnyPrice;

    private String userId;
    private List<OrderInfo> orderInfoList;


    public OrderMergeTempInfo() {
    }

    public OrderMergeTempInfo(List<String> orderIdList, String orderTargetId, BigDecimal orderCnyPrice) {
        this.orderIdList = orderIdList;
        this.orderTargetId = orderTargetId;
        this.orderCnyPrice = orderCnyPrice;
    }

    public OrderMergeTempInfo(String userId, List<OrderInfo> orderInfoList) {
        this.userId = userId;
        this.orderInfoList = orderInfoList;
    }
}
