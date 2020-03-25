package com.converage.service.shop;

import com.converage.architecture.service.BaseService;
import com.converage.constance.ShopConst;
import com.converage.entity.shop.GoodsImg;
import com.converage.entity.shop.GoodsSku;
import com.converage.entity.shop.GoodsSpu;
import com.converage.utils.ValueCheckUtils;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class GoodsImgService extends BaseService {

    /**
     * 新建spu/sku图片记录
     *
     * @param spuId            spuId
     * @param skuId            skuId（在新建spu时，为null）
     * @param introduceImgUrls 介绍图URL列表
     * @param detailImgUrls    详情图URL列表
     * @return
     */
    public void createGoodsImg(String spuId, String skuId, List<String> introduceImgUrls, List<String> detailImgUrls) {
        String errorMsg = "保存图片失败";
        //介绍图
        List<GoodsImg> goodsImgList = new ArrayList<>();
        if(introduceImgUrls.size()>0){
            for (String introduceImg : introduceImgUrls) {
                GoodsImg goodsIntroduceImg = new GoodsImg(spuId, skuId, introduceImg, ShopConst.GOODS_IMG_TYPE_INTRODUCE);
                goodsImgList.add(goodsIntroduceImg);
            }
            ValueCheckUtils.notZero(insertBatch(goodsImgList, false),errorMsg);
        }


        //详情图
        if(detailImgUrls.size()>0){
            goodsImgList = new ArrayList<>();
            for (String detailImgUrl : detailImgUrls) {
                GoodsImg goodsDetailImg = new GoodsImg(spuId, skuId, detailImgUrl, ShopConst.GOODS_IMG_TYPE_DETAIL);
                goodsImgList.add(goodsDetailImg);
            }
            ValueCheckUtils.notZero(insertBatch(goodsImgList, false),errorMsg);
        }
    }


    /**
     * 构建spu的图片列表
     * @param goodsSpuPo
     */
    public void buildImgList(GoodsSpu goodsSpuPo){
        List<GoodsImg> introduceImg = new ArrayList<>();
        List<String> introduceImgUrl = new ArrayList<>();
        List<GoodsImg> detailImg = new ArrayList<>();
        List<String> detailImgUrl = new ArrayList<>();
        for (GoodsImg goodsImg : goodsSpuPo.getAllImg()) {
            Integer imgType = goodsImg.getImgType();
            if (ShopConst.GOODS_IMG_TYPE_INTRODUCE == imgType) {
                introduceImg.add(goodsImg);
                introduceImgUrl.add(goodsImg.getImgUrl());
            } else if (ShopConst.GOODS_IMG_TYPE_DETAIL == imgType) {
                detailImg.add(goodsImg);
                detailImgUrl.add(goodsImg.getImgUrl());
            }
        }

        goodsSpuPo.setIntroduceImg(introduceImg);
        goodsSpuPo.setIntroduceImgUrl(introduceImgUrl);
        goodsSpuPo.setDetailImg(detailImg);
        goodsSpuPo.setDetailImgUrl(detailImgUrl);
    }

    /**
     * 构建sku的图片列表
    */
    public void buildImgList(GoodsSku goodsSkuPo){
        List<GoodsImg> introduceImg = new ArrayList<>();
        List<String> introduceImgUrl = new ArrayList<>();
        List<GoodsImg> detailImg = new ArrayList<>();
        List<String> detailImgUrl = new ArrayList<>();
        for (GoodsImg goodsImg : goodsSkuPo.getAllImg()) {
            Integer imgType = goodsImg.getImgType();
            if (ShopConst.GOODS_IMG_TYPE_INTRODUCE == imgType) {
                introduceImg.add(goodsImg);
                introduceImgUrl.add(goodsImg.getImgUrl());
            } else if (ShopConst.GOODS_IMG_TYPE_DETAIL == imgType) {
                detailImg.add(goodsImg);
                detailImgUrl.add(goodsImg.getImgUrl());
            }
        }

        goodsSkuPo.setIntroduceImg(introduceImg);
        goodsSkuPo.setIntroduceImgUrl(introduceImgUrl);
        goodsSkuPo.setDetailImg(detailImg);
        goodsSkuPo.setDetailImgUrl(detailImgUrl);
    }


}

