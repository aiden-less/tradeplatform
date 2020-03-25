package com.converage.entity.shop;

import com.converage.architecture.mybatis.annotation.Column;
import com.converage.architecture.mybatis.annotation.Id;
import com.converage.architecture.mybatis.annotation.Table;
import lombok.Data;
import org.apache.ibatis.type.Alias;

import java.io.Serializable;
import java.math.BigDecimal;
import java.sql.Timestamp;

@Data
@Alias("OrderMergeInfo")
@Table(name = "order_merge_info")//订单合并id表
public class OrderMergeInfo implements Serializable {
    private static final long serialVersionUID = 7615104190367518104L;

    @Id
    @Column(name = Id)
    private String id;

    @Column(name = User_id)
    private String userId;

    @Column(name = Order_ids)
    private String orderIds;

    @Column(name = Order_price)
    private BigDecimal orderPrice;

    @Column(name = Create_time)
    private Timestamp createTime;//创建时间

    //DB Column name
    public static final String Id = "id";
    public static final String User_id = "user_id";
    public static final String Order_ids = "order_ids";
    public static final String Order_price = "order_price";
    public static final String Create_time = "create_time";

    public OrderMergeInfo() {
    }

    public OrderMergeInfo(String userId, String orderIds, BigDecimal orderPrice) {
        this.userId = userId;
        this.orderIds = orderIds;
        this.orderPrice = orderPrice;
    }

}
