package com.converage.entity.transaction;

import com.converage.architecture.mybatis.annotation.Column;
import com.converage.architecture.mybatis.annotation.Id;
import com.converage.architecture.mybatis.annotation.Table;
import lombok.Data;
import org.apache.ibatis.type.Alias;

import java.io.Serializable;
import java.math.BigDecimal;
import java.sql.Timestamp;

/**
 * Created by 旺旺 on 2020/4/8.
 */
@Data
@Alias("CctOrderDetail")
@Table(name = "cct_order_detail")
public class CctOrderDetail implements Serializable{
    private static final long serialVersionUID = -4385585823851729638L;

    @Id
    @Column(name = Id)
    private String id;

    @Column(name = Order_id)
    private String orderId;

    @Column(name = Trade_coin_name)
    private String tradeCoinName;

    @Column(name = Valuation_coin_name)
    private String valuationCoinName;

    @Column(name = Transaction_unit)
    private BigDecimal transactionUnit;

    @Column(name = Transaction_number)
    private BigDecimal transactionNumber;

    @Column(name = Transaction_poundage)
    private BigDecimal transactionPoundage;

    @Column(name = Create_time)
    private Timestamp createTime;

    //DB Column name
    public static final String Id = "id";
    public static final String Order_id = "order_id";
    public static final String Trade_coin_name = "trade_coin_name";
    public static final String Valuation_coin_name = "valuation_coin_name";
    public static final String Transaction_unit = "transaction_unit";
    public static final String Transaction_number = "transaction_number";
    public static final String Transaction_poundage = "transaction_poundage";
    public static final String Create_time = "create_time";

}
