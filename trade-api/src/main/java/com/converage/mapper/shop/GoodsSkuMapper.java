package com.converage.mapper.shop;

import com.converage.entity.shop.GoodsSku;
import com.converage.entity.shop.OrderItem;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface GoodsSkuMapper {

    /**
     * 删除sku的图片
     *
     * @param spuId
     * @param skuId
     * @return
     */
    Integer deleteSkuImg(@Param("spuId") String spuId, @Param("skuId") String skuId);

    /**
     * sku详情
     *
     * @param skuId
     * @return
     */
    GoodsSku getGoodsSku(String skuId);

    /**
     * 扣除sku库存
     *
     * @param goodsSkuId
     * @param stockNum
     */
    Integer decreaseSkuStock(@Param("skuId") String goodsSkuId, @Param("stockNum") Integer stockNum);


    /**
     * 增加sku库存
     *
     * @param goodsSkuId
     * @param stockNum
     * @return
     */
    Integer increaseSkuStock(@Param("skuId") String goodsSkuId, @Param("stockNum") Integer stockNum);

    /**
     * 还原sku库存
     *
     * @param skuOrderItems
     */
    Integer restoreStock(@Param("orderItem") OrderItem orderItem);
}
