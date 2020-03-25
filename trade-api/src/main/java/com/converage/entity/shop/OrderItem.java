package com.converage.entity.shop;

import com.converage.architecture.mybatis.annotation.Column;
import com.converage.architecture.mybatis.annotation.Id;
import com.converage.architecture.mybatis.annotation.Table;
import lombok.Data;
import org.apache.ibatis.type.Alias;

import java.io.Serializable;
import java.math.BigDecimal;
import java.sql.Timestamp;

import static com.converage.constance.ShopConst.ORDER_STATUS_NOT_PAY;

@Data
@Alias("OrderItem")
@Table(name = "order_item")//订单详情表
public class OrderItem implements Serializable {
    private static final long serialVersionUID = 7195245867662678593L;

    @Id
    @Column(name = Id)
    private String id;

    @Column(name = Order_id)
    private String orderId;//order id

    @Column(name = Goods_spu_id)
    private String goodsSpuId;//spu id

    @Column(name = Goods_sku_id)
    private String goodsSkuId;//sku id

    @Column(name = Number)
    private Integer number;

    @Column(name = Goods_description)
    private String goodsDescription;

    @Column(name = Currency_price)
    private BigDecimal currencyPrice;

    @Column(name = Integral_price)
    private BigDecimal integralPrice;

    @Column(name = Cny_Price)
    private BigDecimal cnyPrice;

    @Column(name = Total_price)
    private BigDecimal totalPrice;

    @Column(name = Currency_reward)
    private BigDecimal currencyReward;

    @Column(name = Freight)
    private BigDecimal freight;

    @Column(name = Status)
    private Integer status;

    @Column(name = Refunds_reason)
    private String refundsReason;

    @Column(name = Refunds_person)
    private String refundsPerson;

    @Column(name = Refunds_phone)
    private String refundsPhone;

    @Column(name = Refunds_address)
    private String refundsAddress;

    //扩展属性
    private Boolean ifSku;
    private Timestamp createTime = new Timestamp(System.currentTimeMillis());
    private String spuId;    //spu id
    private String goodsName;    //spu名称
    private String imgUrl;    //spu默认图片
    private String specJson;    //规格名规格值id json 格式：{"规格名id":"规格值id"}
    private String specStr;    //规格名规格值 json 格式："规格名:规格值 规格名:规格值 规格名:规格值"
    private Integer settlementId;//支付方式
    private String packageUnit;//包装单位
    private String orderItemId;//
    private String integralStr = "TCNY";

    public OrderItem() {
    }

    public OrderItem(GoodsTempInfo goodsTempInfo, Integer buyNumber) {
        this.goodsSpuId = goodsTempInfo.getSpuId();
        this.goodsSkuId = goodsTempInfo.getSkuOrSpuId();
        this.number = buyNumber;
        this.currencyPrice = goodsTempInfo.getCurrencyPrice();
        this.cnyPrice = goodsTempInfo.getCnyPrice();
        this.currencyReward = goodsTempInfo.getCurrencyReward();
        this.ifSku = goodsTempInfo.getIfSku();
        this.status = ORDER_STATUS_NOT_PAY;
    }


    public OrderItem(String goodsSpuId, String goodsSkuId, Integer number, BigDecimal currencyPrice, BigDecimal cnyPrice, BigDecimal currencyReward, Boolean ifSku) {
        this.goodsSpuId = goodsSpuId;
        this.goodsSkuId = goodsSkuId;
        this.number = number;
        this.currencyPrice = currencyPrice;
        this.cnyPrice = cnyPrice;
        this.currencyReward = currencyReward;
        this.ifSku = ifSku;
        this.status = ORDER_STATUS_NOT_PAY;
    }

    //DB Column name
    public static final String Id = "id";
    public static final String Order_id = "order_id";
    public static final String Goods_spu_id = "goods_spu_id";
    public static final String Goods_sku_id = "goods_sku_id";
    public static final String Number = "number";
    public static final String Currency_price = "currency_price";
    public static final String Integral_price = "integral_price";
    public static final String Cny_Price = "cny_Price";
    public static final String Total_price = "total_price";
    public static final String Currency_reward = "currency_reward";
    public static final String Goods_description = "goods_description";
    public static final String Freight = "freight";
    public static final String Status = "status";
    public static final String Refunds_reason = "refunds_reason";
    public static final String Refunds_person = "refunds_person";
    public static final String Refunds_phone = "refunds_phone";
    public static final String Refunds_address = "refunds_address";
}
