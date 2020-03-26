package com.converage.entity;

import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.sql.Timestamp;

/**
 * Created by 旺旺 on 2020/3/17.
 */
@Data
public class CctOrderMatchLog implements Serializable{

    private static final long serialVersionUID = -3094006720391288935L;

    private String id;
    private String buyOrderId;
    private String buyUserId;
    private String sellOrderId;
    private String sellUserId;
    private String tradePairId;
    private BigDecimal doneUnit;
    private BigDecimal doneNumber;
    private Timestamp createTime;

}
