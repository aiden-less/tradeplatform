package com.converage.entity.transaction;

import com.converage.architecture.mybatis.annotation.Column;
import com.converage.architecture.mybatis.annotation.Id;
import com.converage.architecture.mybatis.annotation.Table;
import lombok.Data;
import org.apache.ibatis.type.Alias;

import java.io.Serializable;
import java.math.BigDecimal;
import java.sql.Timestamp;

@Data
@Alias("CctOrder")
@Table(name = "cct_order")
public class CctOrder implements Serializable { //OTC挂单
    private static final long serialVersionUID = -3852161962344228996L;

    @Id
    @Column(name = Id)
    private String id;

    @Column(name = User_id)
    private String userId; //用户id

    @Column(name = Trade_pair_id)
    private String tradePairId;//交易对id

    @Column(name = Trade_pair_name)
    private String tradePairName;//交易对名

    @Column(name = Transaction_type)
    private Integer transactionType; //交易类型 1：买入，2：卖出

    @Column(name = Transaction_type_str)//交易类型字符串
    private String transactionTypeStr;

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
    public static final String Trade_pair_id = "trade_pair_id";
    public static final String Trade_pair_name = "trade_pair_name";
    public static final String Transaction_type = "transaction_type";
    public static final String Transaction_type_str = "transaction_type_str";
    public static final String Transaction_unit = "transaction_unit";
    public static final String Transaction_number = "transaction_number";
    public static final String Transaction_surplus_number = "transaction_surplus_number";
    public static final String Transaction_amount = "transaction_amount";
    public static final String Create_time = "create_time";
    public static final String Finish_time = "finish_time";
    public static final String Status = "status";

}
