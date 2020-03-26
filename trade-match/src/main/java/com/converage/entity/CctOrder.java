package com.converage.entity;

import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.sql.Timestamp;

@Data
public class CctOrder implements Serializable {
    private static final long serialVersionUID = -8273287757715196636L;

    private String id;

    private String userId; //用户id

    private String tradePairId;//交易对id

    private String tradePairName;//交易对名

    private Integer transactionType; //交易类型 1：买入，2：卖出

    private String transactionTypeStr;

    private BigDecimal transactionUnit;//交易单价

    private BigDecimal transactionNumber;//交易数量

    private BigDecimal transactionSurplusNumber;//订单剩余数量

    private BigDecimal transactionAmount;//交易金额

    private Timestamp createTime;//创建时间

    private Timestamp finishTime;//完成时间

    private Integer status;//状态 0：未完成，1：已完成




}
