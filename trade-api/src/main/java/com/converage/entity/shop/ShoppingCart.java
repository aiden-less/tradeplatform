package com.converage.entity.shop;

import com.converage.architecture.mybatis.annotation.Id;
import lombok.Data;
import org.apache.ibatis.type.Alias;

import java.io.Serializable;
import java.math.BigDecimal;

@Data
@Alias("ShoppingCart")
//@Table(name = "shopping_cart")//购物车表
public class ShoppingCart implements Serializable {
    private static final long serialVersionUID = -7605252822243375962L;

    @Id
    private String id;


    private String userId; //user id

    private String shopId; //商铺id

    private String shopName; //商铺名

    private String spuId; //spu id

    private String skuId; //sku id

    private String specIdStr; //规格Id字符串 格式 = [规格名id:规格值id, 规格名id:规格值id]

    private String specValueStr;//规格值字符串 格式 = " 颜色:蓝色 尺码:L "

    private Integer number; //购买数量

    private BigDecimal price; //sku单价

    private String goodsName;
    private String shoppingCartId;
    private BigDecimal usdtPrice;
    private BigDecimal currencyPrice;
    private BigDecimal cnyPrice;
    private String imgUrl;


    public ShoppingCart() {

    }

    public ShoppingCart(
            String userId, String shopId, String shopName, String spuId, String skuId, String specIdJson, String specValueStr, Integer number,
            BigDecimal currencyPrice, BigDecimal usdtPrice, BigDecimal cnyPrice) {
        this.userId = userId;
        this.shopId = shopId;
        this.shopName = shopName;
        this.spuId = spuId;
        this.skuId = skuId;
        this.specIdStr = specIdJson;
        this.specValueStr = specValueStr;
        this.number = number;
        this.currencyPrice = currencyPrice;
        this.usdtPrice = usdtPrice;
        this.cnyPrice = cnyPrice;
    }
}
