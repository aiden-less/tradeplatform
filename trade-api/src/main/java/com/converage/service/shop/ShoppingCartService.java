package com.converage.service.shop;

import com.alibaba.fastjson.JSONObject;
import com.converage.architecture.exception.BusinessException;
import com.converage.architecture.service.BaseService;
import com.converage.client.RedisClient;
import com.converage.constance.RedisKeyConst;
import com.converage.constance.ShopConst;
import com.converage.entity.shop.GoodsSku;
import com.converage.entity.shop.GoodsSpu;
import com.converage.entity.shop.ShopInfo;
import com.converage.entity.shop.ShoppingCart;
import com.google.common.collect.ImmutableMap;
import com.converage.utils.ValueCheckUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;
import java.util.Map;

@Service
public class ShoppingCartService extends BaseService {
    @Autowired
    private GoodsSpecService goodsSpecService;

    @Autowired
    private OrderService orderService;

    @Autowired
    private RedisClient redisClient;

    /**
     * 添加商品到购物车
     *
     * @param userId    用户id
     * @param spuId     spuid
     * @param specIdStr 规格id jsonList 格式为 List 元素格式为"规格名id:规格值id"
     * @param number    购买数量
     */
    public void createShoppingCartItem(String userId, String spuId, String specIdStr, Integer number) {
        List<String> specStrList = JSONObject.parseArray(specIdStr, String.class);
        Collections.sort(specStrList);
        specIdStr = JSONObject.toJSONString(specStrList);

        //2.计算订单总价格
        Map<String, Object> skuWhereMap = ImmutableMap.of(GoodsSku.Spu_id + " = ", spuId, GoodsSku.Spec_json + " = ", specIdStr);
        GoodsSku goodsSku = selectOneByWhereMap(skuWhereMap, GoodsSku.class);

        if (StringUtils.isNotEmpty(specIdStr) && goodsSku == null) {
            throw new BusinessException("暂时无库存");
        }

        GoodsSpu goodsSpu = selectOneById(spuId, GoodsSpu.class);
        ValueCheckUtils.notEmpty(goodsSpu, "未找到商品");

        String shoppingCartId;
        String skuId = goodsSku == null ? "" : goodsSku.getId();


        BigDecimal currencyPrice;
        BigDecimal usdtPrice;
        BigDecimal cnyPrice;
        if (goodsSku != null) { //没有sku
            currencyPrice = goodsSku.getCurrencyPrice();
            usdtPrice = goodsSku.getUsdtPrice();
            cnyPrice = goodsSku.getCnyPrice();
            shoppingCartId = goodsSku.getId();
        } else {
            currencyPrice =  goodsSpu.getCurrencyPrice();
            usdtPrice =  goodsSpu.getUsdtPrice();
            cnyPrice = goodsSpu.getCnyPrice();
            shoppingCartId = spuId;
        }

        String specValueJson = goodsSpecService.strSpecIdToSpecValue(specIdStr);

        ShopInfo shopInfo = selectOneById(spuId, ShopInfo.class);
        String shopName = shopInfo == null ? "TASTE官方商城" : shopInfo.getShopName();
        String shopId = shopInfo == null ? ShopConst.SHOP_OFFICIAL : shopInfo.getId();
        String redisKey = String.format(RedisKeyConst.USER_SHOPPINGCART, userId);

        ShoppingCart shoppingCartItem = (ShoppingCart) redisClient.getHashKey(redisKey, shoppingCartId);
        if (shoppingCartItem != null) {
            shoppingCartItem.setNumber(shoppingCartItem.getNumber() + number);
            redisClient.put(redisKey, shoppingCartId, shoppingCartItem);
        } else {
            ShoppingCart shoppingCart = new ShoppingCart(userId, shopId, shopName, spuId, skuId, specIdStr, specValueJson, number, currencyPrice, usdtPrice,cnyPrice);
            shoppingCart.setId(shoppingCartId);
            shoppingCart.setGoodsName(goodsSpu.getGoodsName());
            shoppingCart.setShoppingCartId(shoppingCartId);
            shoppingCart.setImgUrl(goodsSpu.getDefaultImgUrl());
            redisClient.put(redisKey, shoppingCartId, shoppingCart);
        }
    }

    /**
     * 查询购物车
     *
     * @param userId
     */
    public List<ShoppingCart> queryShoppingCartItem(String userId) {
        String key = String.format(RedisKeyConst.USER_SHOPPINGCART, userId);
        return (List<ShoppingCart>) redisClient.getHashValues(key);
    }

    /**
     * 统计购物车的商品数量
     *
     * @param userId
     * @return
     */
    public Integer countShoppingCartGoods(String userId) {
        Integer i = 0;
        List<ShoppingCart> shoppingCarts = queryShoppingCartItem(userId);
        for (ShoppingCart shoppingCart : shoppingCarts) {
            i += shoppingCart.getNumber();
        }
        return i;
    }


    /**
     * 删除购物车条目
     *
     * @param userId
     * @param shoperCartId
     */
    public void deleteShoppingCartItem(String userId, String shoperCartId) {
        ValueCheckUtils.notEmpty(shoperCartId, "请选择删除列表");
        String redisKey = String.format(RedisKeyConst.USER_SHOPPINGCART, userId);
        redisClient.delete(redisKey, shoperCartId);
    }

    /**
     * 更新购物车条目数目
     *
     * @param userId
     * @param shoppingCartId
     * @param number
     */
    public void updateNumber(String userId, String shoppingCartId, Integer number) {
        if (number <= 0) {
            throw new BusinessException("数量必须大于0");
        }
        String redisKey = String.format(RedisKeyConst.USER_SHOPPINGCART, userId);
        ShoppingCart shoppingCartItem = (ShoppingCart) redisClient.getHashKey(redisKey, shoppingCartId);
        shoppingCartItem.setNumber(number);
        redisClient.put(redisKey, shoppingCartId, shoppingCartItem);
    }
}
