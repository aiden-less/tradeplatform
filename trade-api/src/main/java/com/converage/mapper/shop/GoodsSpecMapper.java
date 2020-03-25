package com.converage.mapper.shop;

import com.converage.entity.shop.GoodsSpecName;
import org.springframework.stereotype.Repository;

@Repository
public interface GoodsSpecMapper {

    /**
     * 获取规格名详情
     * @param specNameId
     * @return
     */
    GoodsSpecName getGoodsSpecName(String specNameId);
}
