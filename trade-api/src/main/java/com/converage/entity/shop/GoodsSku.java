package com.converage.entity.shop;

import com.converage.architecture.mybatis.annotation.Column;
import com.converage.architecture.mybatis.annotation.Id;
import com.converage.architecture.mybatis.annotation.Table;
import lombok.Data;
import org.apache.ibatis.type.Alias;

import java.io.Serializable;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.List;
import java.util.Map;

@Data
@Alias("GoodsSku")
@Table(name = "goods_sku")//商品sku表
public class GoodsSku implements Serializable{
    private static final long serialVersionUID = 3047394843767093980L;

    @Id
    @Column(name = Id)
    private String id;

    @Column(name = Sku_no)
    private String skuNo; //sku编号

    @Column(name = Sku_name)
    private String skuName;//spu名称(冗余spu_name)

    @Column(name = Currency_price)
    private BigDecimal currencyPrice;//平台币价格

    @Column(name = Integral_price)
    private BigDecimal integralPrice;//积分价格

    @Column(name = Usdt_price)
    private BigDecimal usdtPrice;//USDT价格

    @Column(name = Cny_price)
    private BigDecimal cnyPrice;//人民币价格

    @Column(name = Currency_reward)
    private BigDecimal currencyReward; //代币奖励

    @Column(name = Stock)
    private Integer stock;//库存

    @Column(name = Spu_id)
    private String spuId;//spuId（goods_spud表id）

    @Column(name = Settlement_id)
    private Integer settlementId;//支付方式id

    @Column(name = Spec_json)
    private String specJson;//规格json 格式：["规格名id:规格值id","规格名id:规格值id"]

    @Column(name = Shop_id)
    private String shopId;//店铺id

    @Column(name = Create_time)
    private Timestamp createTime;

    @Column(name = Update_time)
    private Timestamp updateTime;

    //ShopConst.GOODS_STATUS_*
    @Column(name = Status)
    private Integer status;//sku状态

    @Column(name = If_valid)
    private Boolean ifValid;//是否有效

    //扩展属性
    //规格map
    private Map<String,Object> specJsonMap;

    private List<String> imgUrl;
    //所有图片集合
    private List<GoodsImg> allImg;
    //详情图url
    private List<String> detailImgUrl;
    //详情图对象列表
    private List<GoodsImg> detailImg;
    //介绍图url
    private List<String> introduceImgUrl;
    //介绍图对象列表
    private List<GoodsImg> introduceImg;

    private String integralStr = "TCNY";

    //DB Column name
    public static final String Id = "id";
    public static final String Sku_no = "sku_no";
    public static final String Sku_name = "sku_name";
    public static final String Price = "price";
    public static final String Stock = "stock";
    public static final String Spu_id = "spu_id";
    public static final String Currency_price = "currency_price";
    public static final String Integral_price = "integral_price";
    public static final String Currency_reward = "currency_reward";
    public static final String Usdt_price = "usdt_price";
    public static final String Cny_price = "cny_price";
    public static final String Settlement_id = "settlement_id";
    public static final String Spec_json = "spec_json";
    public static final String Shop_id = "shop_id";
    public static final String Create_time = "create_time";
    public static final String Update_time = "update_time";
    public static final String Status = "status";
    public static final String If_valid = "if_valid";
}
