package com.converage.controller.app.req;

import com.converage.entity.encrypt.EncryptEntity;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class TradeOrderPayReq extends EncryptEntity {
    private Integer transactionType; //交易类型
    private String tradePairId;  // 交易对id
    private BigDecimal transactionUnit; //交易单价
    private BigDecimal transactionNumber; //交易数量
    private String payPassword; //支付密码
}
