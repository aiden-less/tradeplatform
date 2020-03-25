package com.converage.controller.app.req;

import com.converage.entity.shop.ShoppingCart;
import lombok.Data;

@Data
public class ShopOrderSkuReq {
    private String spuId; //spuId
    private String specIdStr; //sku name value 组合字符串 String元素格式为 "[规格名Id:规格值id]"
    private Integer number;
    private String shoppingCartId;
    private String shopId;

    public ShopOrderSkuReq() {
    }

    public ShopOrderSkuReq(ShoppingCart shoppingCart) {
        this.spuId = shoppingCart.getSpuId();
        this.specIdStr = shoppingCart.getSpecIdStr();
        this.number = shoppingCart.getNumber();
        this.shoppingCartId = shoppingCart.getId();
        this.shopId = shoppingCart.getShopId();
    }

    public ShopOrderSkuReq(String spuId, String specIdStr, Integer number) {
        this.spuId = spuId;
        this.specIdStr = specIdStr;
        this.number = number;
    }
}
