package com.converage.entity.assets;

import com.converage.architecture.mybatis.annotation.Column;
import com.converage.architecture.mybatis.annotation.Id;
import com.converage.architecture.mybatis.annotation.Table;
import lombok.Data;
import org.apache.ibatis.type.Alias;

import java.io.Serializable;
import java.math.BigDecimal;
import java.sql.Timestamp;

/**
 * Created by 旺旺 on 2020/3/16.
 */
@Data
@Alias("CctFrozenAssets")
@Table(name = "cct_frozen_assets")
public class CctFrozenAssets implements Serializable{
    private static final long serialVersionUID = -6119424652382143591L;

    @Id
    @Column(name = Id)
    private String id;

    @Column(name = Coin_id)
    private String coinId; //币种id

    @Column(name = User_id)
    private String userId; //用户id

    @Column(name = Order_id)
    private String orderId; //订单Id

    @Column(name = Assets_amount)
    private BigDecimal assetsAmount; //冻结资产数目

    @Column(name = If_refund)
    private Boolean ifRefund; //是否已退回

    @Column(name = Create_time)
    private Timestamp createTime; //创建时间

    //DB Column name
    public static final String Id = "id";
    public static final String Coin_id = "coin_id";
    public static final String User_id = "user_id";
    public static final String Order_id = "order_id";
    public static final String Assets_amount = "assets_amount";
    public static final String If_refund = "if_refund";
    public static final String Create_time = "create_time";


}
