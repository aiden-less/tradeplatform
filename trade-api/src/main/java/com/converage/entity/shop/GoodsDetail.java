package com.converage.entity.shop;

import lombok.Data;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.List;


@Data
public class GoodsDetail {//商品详情实体

    //spu id
    private String spuId;

    //spu规格id
    private String spuSpecId;

    //规格名id
    private String specNameId;

    //规格名称
    private String specName;

    //规格值id
    private String specValueId;

    //规格值
    private String specValue;

    //sku Id
    private String skuId;

    //商品名称
    private String goodsName;

    //商品描述
    private String goodsDescription;

    //库存
    private Integer stock;


    private BigDecimal currencyPrice;
    private BigDecimal integralPrice;
    private String integralStr = "TCNY";

    private BigDecimal cnyPrice;
    private BigDecimal currencyReward;

    private BigDecimal usdtPrice;



    //sku创建时间
    private Timestamp createTime;

    //规格图
    private String specImgUrl;

    //介绍图片url
    private List<String> introduceImgList;

    //详情图片url
    private List<String> detailImgList;

    //skuList
    private List<GoodsSpec> goodsSpecList;

    //是否有收藏
    private Boolean isCollect;

    //包装单位
    private String packageUnit;

    //背景色
    private String backgroundColor;

    //运费
    private BigDecimal freight;

    //折扣运费
    private BigDecimal discountFreight;

    //收藏数
    private Integer countCollection;

    //购买数
    private Integer countBuy;

    //商品类型
    private Integer spuType;

    //价格图标
    private String priceIcon = "https://taste-common-img.oss-cn-hongkong.aliyuncs.com/2019-07-05/aa67a886-7465-48bc-9894-05e7d5f63d3c.png";
}
