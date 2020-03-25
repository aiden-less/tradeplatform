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
 * Created by 旺旺 on 2020/3/17.
 */
@Data
@Alias("CctOrderLog")
@Table(name = "cct_order_log")
public class CctOrderLog implements Serializable{
    private static final long serialVersionUID = 3171170362479226604L;

    @Id
    @Column(name = Id)
    private String id;

    @Column(name = Buy_order_id)
    private String buyOrderId;

    @Column(name = Buy_user_id)
    private String buyUserId;

    @Column(name = Sell_order_id)
    private String sellOrderId;

    @Column(name = Sell_user_id)
    private String sellUserId;

    @Column(name = Trade_pair_id)
    private String tradePairId;

    @Column(name = Done_unit)
    private BigDecimal doneUnit;

    @Column(name = Done_number)
    private BigDecimal doneNumber;

    @Column(name = Create_time)
    private Timestamp createTime;


    //DB Column name
    public static final String Id = "id";
    public static final String Buy_order_id = "buy_order_id";
    public static final String Buy_user_id = "buy_user_id";
    public static final String Sell_order_id = "sell_order_id";
    public static final String Sell_user_id = "sell_user_id";
    public static final String Trade_pair_id = "trade_pair_id";
    public static final String Done_unit = "done_unit";
    public static final String Done_number = "done_number";
    public static final String Create_time = "create_time";

}
