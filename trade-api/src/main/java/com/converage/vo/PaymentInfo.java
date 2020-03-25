package com.converage.vo;

import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
public class PaymentInfo {

    private Integer settlementId; //支付方式

    private BigDecimal balance; //余额

    private List<PaymentMethod> methodList; // 支付方式列表

    public PaymentInfo() {
    }

    public PaymentInfo(Integer settlementId, BigDecimal balance, List<PaymentMethod> methodList) {
        this.settlementId = settlementId;
        this.balance = balance;
        this.methodList = methodList;
    }
}
