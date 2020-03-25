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
 * Created by 旺旺 on 2020/3/20.
 */
@Data
@Alias("LctMerchantOrder")
@Table(name = "lct_merchant_order")//法币商户订单
public class LctMerchantOrder implements Serializable {
    private static final long serialVersionUID = -1163052345090569203L;

    @Id
    @Column(name = Id)
    private String id;

    @Column(name = User_id)
    private String userId;

    @Column(name = Coin_id)
    private String coinId;

    @Column(name = Transaction_type)
    private Integer transactionType; //交易类型 1：买入，2：卖出

    @Column(name = Transaction_unit)
    private BigDecimal transactionUnit;//交易单价

    @Column(name = Transaction_number)
    private BigDecimal transactionNumber;//交易数量

    @Column(name = Transaction_surplus_number)
    private BigDecimal transactionSurplusNumber;//订单剩余数量

    @Column(name = Transaction_amount)
    private BigDecimal transactionAmount;//交易金额

    @Column(name = Create_time)
    private Timestamp createTime;//创建时间

    @Column(name = Finish_time)
    private Timestamp finishTime;//完成时间

    @Column(name = Status)
    private Integer status;//状态 0：未完成，1：已完成


    //DB Column name
    public static final String Id = "id";
    public static final String User_id = "user_id";
    public static final String Coin_id = "coin_id";
    public static final String Transaction_type = "transaction_type";
    public static final String Transaction_unit = "transaction_unit";
    public static final String Transaction_number = "transaction_number";
    public static final String Transaction_surplus_number = "transaction_surplus_number";
    public static final String Transaction_amount = "transaction_amount";
    public static final String Create_time = "create_time";
    public static final String Finish_time = "finish_time";
    public static final String Status = "status";
}
