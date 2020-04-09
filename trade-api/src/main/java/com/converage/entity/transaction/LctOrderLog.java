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
@Alias("LctOrderLog")
@Table(name = "lct_order_log")//法币交易订单记录
public class LctOrderLog implements Serializable {

    private static final long serialVersionUID = 7604177186210568764L;
    @Id
    @Column(name = Id)
    private String id;

    @Column(name = Coin_id)
    private String coinId;

    @Column(name = Order_no)
    private String orderNo; //订单号 也是散户订单和商户订单交易共同标识

    @Column(name = Transaction_type)
    private Integer transactionType;

    @Column(name = Lct_merchant_order_id)
    private String lctMerchantOrderId;

    @Column(name = User_id)
    private String userId; //用户Id

    @Column(name = Buyer_user_id)
    private String buyerUserId;  //买家用户id

    @Column(name = Seller_user_id)
    private String sellerUserId;  //卖家用户id

    @Column(name = Done_unit)
    private BigDecimal doneUnit; //成交价

    @Column(name = Done_number)
    private BigDecimal doneNumber; //成交量

    @Column(name = Create_time)
    private Timestamp createTime;

    @Column(name = If_pay)
    private Boolean ifPay;

    @Column(name = Uid)
    private String uid;

    @Column(name = Status)
    private Integer status;

    //DB Column name
    public static final String Id = "id";
    public static final String Coin_id = "coin_id";
    public static final String Order_no = "order_no";
    public static final String Transaction_type = "transaction_type";
    public static final String Lct_merchant_order_id = "lct_merchant_order_id";
    public static final String User_id = "user_id";
    public static final String Buyer_user_id = "buyer_user_id";
    public static final String Seller_user_id = "seller_user_id";
    public static final String Done_unit = "done_unit";
    public static final String Done_number = "done_number";
    public static final String Create_time = "create_time";
    public static final String If_pay = "if_pay";
    public static final String Uid = "uid";
    public static final String Status = "status";

}
