package com.converage.entity.shop;


import com.converage.architecture.dto.Pagination;
import com.converage.architecture.mybatis.annotation.Column;
import com.converage.architecture.mybatis.annotation.Id;
import com.converage.architecture.mybatis.annotation.Table;
import com.converage.utils.OrderNoUtils;
import lombok.Data;
import org.apache.ibatis.type.Alias;

import java.io.Serializable;
import java.math.BigDecimal;
import java.sql.Timestamp;

@Data
@Alias("OrderInfo")
@Table(name = "order_info")//订单基本信息表
public class OrderInfo implements Serializable {

    private static final long serialVersionUID = 5290596013449535746L;

    @Id
    @Column(name = Id)
    private String id;

    @Column(name = Order_no)
    private String orderNo; //订单编号

    @Column(name = Settlement_id)
    private Integer settlementId; //支付方式

    @Column(name = User_id)
    private String userId; //用户id

    @Column(name = Shop_id)
    private String shopId;//店铺id

    @Column(name = User_name)
    private String userName; //用户昵称

    @Column(name = Freight)
    private BigDecimal freight;//运费

    @Column(name = Currency_price)
    private BigDecimal currencyPrice;

    @Column(name = Integral_price)
    private BigDecimal integralPrice;

    @Column(name = Cny_price)
    private BigDecimal cnyPrice;

    @Column(name = Order_price)
    private BigDecimal orderPrice;//订单支付的金额

    @Column(name = Status)
    private Integer status;//订单状态

    @Column(name = Status_str)
    private String statusStr;//状态描述

    @Column(name = If_investigation)
    private Boolean ifInvestigation; //是否已提交问卷调查

    @Column(name = If_pay)
    private Boolean ifPay;//是否已支付

    @Column(name = If_valid)
    private Boolean ifValid;//是否有效

    @Column(name = Logistic_number)
    private String logisticNumber; //物流编码

    @Column(name = Create_time)
    private Timestamp createTime;//创建时间

    @Column(name = Pay_time)
    private Timestamp payTime;//支付时间

    @Column(name = Order_type)
    private Integer orderType;//订单类型

    @Column(name = Shoper_name)
    private String shoperName;//收货人姓名

    @Column(name = Shoper_address)
    private String shoperAddress;//收货人地址

    @Column(name = Shoper_tel)
    private String shoperTel;//收货人电话号码

    @Column(name = Cancel_reason)
    private String cancelReason; //退货原因

    @Column(name = Shopping_cart_ids)
    private String shoppingCartIds; //购物车条目ids

    //扩展属性
    private Timestamp startTime;
    private Timestamp endTime;
    private Pagination pagination;

    private String integralStr = "TCNY";

    public OrderInfo() {
    }


    //DB Column name
    public static final String Id = "id";
    public static final String Order_no = "order_no";
    public static final String Settlement_id = "settlement_id";
    public static final String User_id = "user_id";
    public static final String Shop_id = "shop_id";
    public static final String Shop_name = "shop_name";
    public static final String User_name = "user_name";
    public static final String Currency_price = "currency_price";
    public static final String Integral_price = "integral_price";
    public static final String Cny_price = "cny_price";
    public static final String Order_price = "order_price";
    public static final String Freight = "freight";
    public static final String Status = "status";
    public static final String Status_str = "status_str";
    public static final String If_investigation = "if_investigation";
    public static final String If_pay = "if_pay";
    public static final String Logistic_number = "logistic_number";
    public static final String Create_time = "create_time";
    public static final String If_valid = "if_valid";
    public static final String Pay_time = "pay_time";
    public static final String Order_type = "order_type";
    public static final String Shoper_name = "shoper_name";
    public static final String Shoper_address = "shoper_address";
    public static final String Shoper_tel = "shoper_tel";
    public static final String Cancel_reason = "cancel_reason";
    public static final String Shopping_cart_ids = "shopping_cart_ids";

    public void buildOrderInfo(
            Integer settlementId, String userId, String shopId, String userName, BigDecimal currencyPrice,BigDecimal integralPrice, BigDecimal cnyPrice,
            Integer orderType, Integer status, ShoppingAddress shoppingAddress
    ) {
        this.orderNo = OrderNoUtils.buildOrderNo();
        this.settlementId = settlementId;
        this.userId = userId;
        this.shopId = shopId;
        this.userName = userName;
        this.currencyPrice = currencyPrice;
        this.integralPrice = integralPrice;
        this.cnyPrice = cnyPrice;
        this.orderType = orderType;
        this.status = status;
        if (shoppingAddress != null) {
            this.shoperName = shoppingAddress.getShoperName();
            this.shoperAddress = shoppingAddress.getShoperProvince() + shoppingAddress.getShoperCity() + shoppingAddress.getShoperDistrict() + shoppingAddress.getShoperAddress();
            this.shoperTel = shoppingAddress.getShoperTel();
        }
        this.createTime = new Timestamp(System.currentTimeMillis());

    }

}
