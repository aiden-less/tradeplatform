package com.converage.service.shop;

import com.alibaba.fastjson.JSONObject;
import com.converage.architecture.dto.Pagination;
import com.converage.architecture.exception.BusinessException;
import com.converage.architecture.service.BaseService;
import com.converage.constance.InformationConst;
import com.converage.entity.information.Article;
import com.converage.utils.ValueCheckUtils;
import com.converage.constance.ShopConst;
import com.converage.entity.shop.*;
import com.converage.mapper.shop.GoodsSpuMapper;
import com.google.common.collect.ImmutableMap;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.*;

import static com.converage.constance.ShopConst.GOODS_STATUS_INSALE;

@Service
public class GoodsSpuService extends BaseService {

    @Autowired
    private GoodsSpecService goodsSpecService;

    @Autowired
    private GoodsSpuMapper goodsSpuMapper;

    @Autowired
    private GoodsImgService goodsImgService;

    @Autowired
    private GoodsBrandService goodsBrandService;

    @Autowired
    private GoodsCategoryService goodsCategoryService;

    @Autowired
    private ShopInfoService shopInfoService;

    /**
     * 新增商品基本信息
     *
     * @param goodsSpu
     * @param defaultImgUrl 商品默认图片url
     * @param detailImgUrls 商品详情图片urls
     */
    public void saveGoodsSpu(GoodsSpu goodsSpu, String defaultImgUrl, String specImgUrl, List<String> introduceImgUrls, List<String> detailImgUrls) {
        ValueCheckUtils.notEmpty(goodsSpu.getGoodsName(), "商品名不能为空");
        ValueCheckUtils.notEmpty(goodsSpu.getCurrencyPrice(), "商品TC价格不能为空");
        ValueCheckUtils.notEmpty(goodsSpu.getCnyPrice(), "商品人民币价格不能为空");
//        ValueCheckUtils.notEmpty(goodsSpu.getUsdtPrice(), "商品USDT价格不能为空");
        if (BigDecimal.ZERO.compareTo(goodsSpu.getCurrencyPrice()) >= 0 || BigDecimal.ZERO.compareTo(goodsSpu.getCnyPrice()) >= 0) {
            throw new BusinessException("商品TC价格和人民币价格不能少0");
        }
        ValueCheckUtils.notEmpty(defaultImgUrl, "商品默认图片不能为空");
//        ValueCheckUtils.notEmpty(specImgUrl, "商品规格参数图片不能为空");
//        ValueCheckUtils.notEmpty(introduceImgUrls, "商品介绍图片不能为空");
//        ValueCheckUtils.notEmpty(detailImgUrls, "商品详情图片不能为空");

        String spuId = goodsSpu.getId();
        if (StringUtils.isNoneBlank(spuId)) {
            GoodsSpu goodsSpuPo = selectOneById(spuId, GoodsSpu.class);
            ValueCheckUtils.notEmpty(goodsSpuPo, "未找到商品记录");
            //更新spu
            ValueCheckUtils.notZero(updateIfNotNull(goodsSpu), "更新商品失败");
            goodsSpuMapper.deleteSpuImg(spuId);
        } else {
            //新增商品
            String spuNo = System.currentTimeMillis() + "" + (int) ((Math.random() * 9 + 1) * 1000);
            goodsSpu.setDefaultImgUrl(defaultImgUrl);
            goodsSpu.setSpecImgUrl(specImgUrl);
            goodsSpu.setSpuNo(spuNo);
            ValueCheckUtils.notZero(insertIfNotNull(goodsSpu), "上传商品失败");
        }

        goodsImgService.createGoodsImg(goodsSpu.getId(), null, introduceImgUrls, detailImgUrls);
    }

    /**
     * 商品基本信息列表
     *
     * @param goodsSpu   spu查询条件尸体
     * @param pagination 分页实体
     * @return
     */
    public List<GoodsSpu> listGoodsSpu(GoodsSpu goodsSpu, Pagination pagination) {
        return goodsSpuMapper.listGoodsSpu(goodsSpu.getGoodsName(), goodsSpu.getCategoryId(), goodsSpu.getBrandId(), goodsSpu.getSpuType(), goodsSpu.getStatus(), goodsSpu.getStartTime(), goodsSpu.getEndTime(), pagination);
    }

    /**
     * 查询单个spu
     *
     * @param spuId
     * @return
     */
    public GoodsSpu getGoodsSpu(String spuId) {
        GoodsSpu goodsSpuPo = goodsSpuMapper.getGoodsSpu(spuId);
        goodsImgService.buildImgList(goodsSpuPo);
        return goodsSpuPo;
    }

    public GoodsSpu validateGoodsSpu(String spuId) {
        Map<String, Object> spuWhereMap = ImmutableMap.of(GoodsSpu.Id + " = ", spuId, GoodsSpu.If_valid + " = ", true);
        GoodsSpu goodsSpu = selectOneByWhereMap(spuWhereMap, GoodsSpu.class);
        ValueCheckUtils.notEmpty(goodsSpu, "未找到商品");
        if (goodsSpu.getStatus() == ShopConst.GOODS_STATUS_OUTSALE) {
            throw new BusinessException("商品已下架");
        }

        if (goodsSpu.getStatus() == ShopConst.GOODS_STATUS_FROZEN) {
            throw new BusinessException("商品已冻结");
        }
        return goodsSpu;
    }

    /**
     * 商品详细信息
     *
     * @param spuId
     * @return
     */
    public GoodsDetail getGoodsDetail(String userId, String spuId) {
        GoodsDetail goodsDetail = new GoodsDetail();
        GoodsSpu goodsSpu = selectOneById(spuId, GoodsSpu.class);
        if (goodsSpu == null || !goodsSpu.getIfValid()) {
            throw new BusinessException("商品不存在");
        }

        if (goodsSpu.getStatus() == ShopConst.GOODS_STATUS_OUTSALE) {
            throw new BusinessException("商品已下架");
        }

        if (goodsSpu.getStatus() == ShopConst.GOODS_STATUS_FROZEN) {
            throw new BusinessException("商品已冻结");
        }

        goodsDetail.setSpuId(goodsSpu.getId());

        goodsDetail.setGoodsName(goodsSpu.getGoodsName());
        goodsDetail.setGoodsDescription(goodsSpu.getGoodsDescription());
        goodsDetail.setCurrencyPrice(goodsSpu.getCurrencyPrice());
        goodsDetail.setIntegralPrice(goodsSpu.getIntegralPrice());
        goodsDetail.setCnyPrice(goodsSpu.getCnyPrice());
        goodsDetail.setCurrencyReward(goodsSpu.getCurrencyReward());
        goodsDetail.setUsdtPrice(goodsSpu.getUsdtPrice());
        goodsDetail.setSpecImgUrl(goodsSpu.getSpecImgUrl());
        goodsDetail.setPackageUnit(goodsSpu.getAttribute());
        goodsDetail.setFreight(goodsSpu.getFreight());
        goodsDetail.setCountCollection(goodsSpuMapper.countCollection(goodsSpu.getId()));
        goodsDetail.setCountBuy(goodsSpuMapper.countBuy(goodsSpu.getId()));
        goodsDetail.setSpuType(goodsSpu.getSpuType());


        //会员享有折扣运费
//        if (user.getMemberType() == UserConst.USER_MEMBER_TYPE_COMMON) {
//            goodsDetail.setDiscountFreight(BigDecimalUtils.multiply(goodsSpu.getFreight(), BigDecimal.valueOf(0.95)));
//        }

//        //检查用户是否有收藏商品
//        Map<String, Object> whereMap_ = ImmutableMap.of(GoodsCollection.User_id + "=", userId, GoodsCollection.Spu_id + "=", spuId);
//        GoodsCollection goodsCollection = selectOneByWhereMap(whereMap_, GoodsCollection.class);
//        if (goodsCollection != null) {
//            goodsDetail.setIsCollect(true);
//        } else {
//            goodsDetail.setIsCollect(false);
//        }

        //查询spu的sku列表
        Map<String, Object> skuWhereMap = ImmutableMap.of(GoodsSku.Spu_id + "=", spuId, GoodsSku.If_valid + "=", true);
        List<GoodsSku> goodsSkuList = selectListByWhereMap(skuWhereMap, GoodsSku.class);

        //key:specName
        Map<String, List<GoodsSkuSelectValue>> skuListMap = new HashMap<>();

        List<GoodsSpec> goodsSpecList = new ArrayList<>();
        //1.获取商品所有的sku信息
        //2.按商品skuJson key进行分类
        //3.构造默认选中sku信息,商品图片信息
        for (GoodsSku goodsSku : goodsSkuList) {
            List<String> skuList = JSONObject.parseArray(goodsSku.getSpecJson(), String.class);
            if (skuList == null)
                break;
            for (String skuArrayStr : skuList) {
                String[] skuArray = skuArrayStr.split(":");
                String specNameId = skuArray[0];//规格名id
                String specValueId = skuArray[1];//规格值id

                String specName = goodsSpecService.getGoodSpecNameById(specNameId).getSpecName();
                String specValue = goodsSpecService.getGoodsSpecValueById(specValueId).getSpecValue();

                GoodsSkuSelectValue goodsSkuSelectValue = new GoodsSkuSelectValue(specNameId, specValueId, specValue);
                List<GoodsSkuSelectValue> goodsSkuSelectValueList;

                //如果规格名(key)于skuListMap已存在,则更新值
                if (skuListMap.containsKey(specName)) {
                    goodsSkuSelectValueList = skuListMap.get(specName);
                } else {//如果不存在，新建List
                    goodsSkuSelectValueList = new ArrayList<>();
                }

                if (!goodsSkuSelectValueList.contains(goodsSkuSelectValue)) {
                    goodsSkuSelectValueList.add(goodsSkuSelectValue);
                }
                skuListMap.put(specName, goodsSkuSelectValueList);
            }
        }

        //skuListMap转换成GoodsSpecList
        for (Map.Entry<String, List<GoodsSkuSelectValue>> entry : skuListMap.entrySet()) {
            goodsSpecList.add(new GoodsSpec(entry.getKey(), entry.getValue()));
        }
        goodsDetail.setGoodsSpecList(goodsSpecList);

        Map<String, Object> whereMap = ImmutableMap.of(GoodsImg.Spu_id + " = ", spuId);
        List<GoodsImg> goodsImgList = selectListByWhereMap(whereMap, GoodsImg.class);
        List<String> introduceImgList = new ArrayList<>(5);
        List<String> detailImgList = new ArrayList<>(5);
        for (GoodsImg goodsImg : goodsImgList) {
            Integer imgType = goodsImg.getImgType();
            if (ShopConst.GOODS_IMG_TYPE_INTRODUCE == imgType) {//介绍图
                introduceImgList.add(goodsImg.getImgUrl());
            } else if (ShopConst.GOODS_IMG_TYPE_DETAIL == imgType) {//详情图
                detailImgList.add(goodsImg.getImgUrl());
            }
        }
        goodsDetail.setIntroduceImgList(introduceImgList);
        goodsDetail.setDetailImgList(detailImgList);
        return goodsDetail;
    }

    /**
     * 收藏商品
     *
     * @param userId
     * @param spuId
     */
    public void updateGoodCollection(String userId, String spuId) {
        GoodsSpu goodsSpu = selectOneById(spuId, GoodsSpu.class);
        ValueCheckUtils.notEmpty(goodsSpu, "未找到商品");

        Map<String, Object> whereMap = ImmutableMap.of(GoodsCollection.User_id + "=", userId, GoodsCollection.Spu_id + "=", spuId);
        GoodsCollection goodsCollection = selectOneByWhereMap(whereMap, GoodsCollection.class);

        if (goodsCollection == null) {//收藏商品
            goodsCollection = new GoodsCollection(userId, spuId, goodsSpu.getGoodsName(), goodsSpu.getGoodsDescription(), goodsSpu.getCurrencyPrice(), goodsSpu.getDefaultImgUrl(), goodsSpu.getSpuType());
            Integer i = insertIfNotNull(goodsCollection);
            if (i == 0) {
                throw new BusinessException("收藏商品失败");
            }
        } else { //取消收藏商品
            Integer i = delete(goodsCollection);
            if (i == 0) {
                throw new BusinessException("取消收藏失败");
            }
        }
    }

    /**
     * 用户的收藏商品列表
     *
     * @param userId
     * @param pagination
     * @return
     */
    public List<GoodsCollection> listGoodsCollection(String userId, Pagination pagination, Integer spuType) {
        return goodsSpuMapper.listGoodsCollection(userId, pagination, spuType);
    }


    /**
     * 增删改查Spu
     *
     * @param goodsSpu
     * @param operatorType
     * @return
     */
    public Object operator(GoodsSpu goodsSpu, Integer operatorType, String defaultImgUrl, String specImgUrl, List<String> introduceImgUrl, List<String> detailImgUrl, Pagination pagination) {
        Object o = null;
        switch (operatorType) {
            case ShopConst.OPERATOR_TYPE_INSERT: //添加
                saveGoodsSpu(goodsSpu, defaultImgUrl, specImgUrl, introduceImgUrl, detailImgUrl);
                break;

            case ShopConst.OPERATOR_TYPE_DELETE: //删除
                GoodsSpu delGoodsSpu = new GoodsSpu();
                delGoodsSpu.setId(goodsSpu.getId());
                delGoodsSpu.setIfValid(false);
                o = "删除商品成功";
                if (updateIfNotNull(delGoodsSpu) == 0) {
                    throw new BusinessException("删除商品失败");
                }
                break;

            case ShopConst.OPERATOR_TYPE_QUERY_LIST: //查询列表
                o = listGoodsSpu(goodsSpu, pagination);
                break;

            case ShopConst.OPERATOR_TYPE_QUERY_DETAIL: //查询单个
                o = getGoodsSpu(goodsSpu.getId());
                break;

            case ShopConst.OPERATOR_TYPE_FROZEN://冻结/解冻商品
                GoodsSpu frozenGoodsSpu = new GoodsSpu();
                frozenGoodsSpu.setId(goodsSpu.getId());
                if (ShopConst.GOODS_STATUS_FROZEN == goodsSpu.getStatus()) {
                    o = "解冻商品";
                    frozenGoodsSpu.setStatus(ShopConst.GOODS_STATUS_OUTSALE);
                } else {
                    o = "冻结商品";
                    frozenGoodsSpu.setStatus(ShopConst.GOODS_STATUS_FROZEN);
                }
                if (updateIfNotNull(frozenGoodsSpu) == 0) {
                    throw new BusinessException(o + "失败");
                }
                o += "成功";
                break;

            case ShopConst.OPERATOR_TYPE_SHELF://上架/下架
                GoodsSpu shelfGoodsSpu = new GoodsSpu();
                shelfGoodsSpu.setId(goodsSpu.getId());
                if (ShopConst.GOODS_STATUS_OUTSALE == goodsSpu.getStatus()) {
                    o = "上架商品";
                    shelfGoodsSpu.setStatus(GOODS_STATUS_INSALE);
                } else {
                    o = "下架商品";
                    shelfGoodsSpu.setStatus(ShopConst.GOODS_STATUS_OUTSALE);
                }
                if (updateIfNotNull(shelfGoodsSpu) == 0) {
                    throw new BusinessException(o + "失败");
                }
                o += "成功";
                break;
        }
        return o;
    }

    /**
     * 根据spuId查询规格名
     *
     * @param spuId
     * @return
     */
    public List<GoodsSpecName> listSpecName(String spuId) {
        return goodsSpuMapper.listSpuSpec(spuId);
    }

    /**
     * 查询全部规格名
     *
     * @return
     */
    public List<GoodsSpecName> allSpecName() {
        return goodsSpuMapper.allSpuSpec();
    }

    /**
     * 更新规格名列表
     *
     * @param goodsSpu
     */
    public void updateSpuName(GoodsSpu goodsSpu) {
        String spuId = goodsSpu.getId();
        List<String> specNameIdList = goodsSpu.getSpecNameIdList();
        goodsSpuMapper.deleteSpuSpecName(spuId);
        goodsSpuMapper.deleteSku(spuId);
        List<GoodSpuSpec> goodSpuSpecs = new ArrayList<>();
        for (String specNameId : specNameIdList) {
            goodSpuSpecs.add(new GoodSpuSpec(spuId, specNameId));
        }
        insertBatch(goodSpuSpecs, false);
    }

    /**
     * 获取品牌，类目，商铺信息
     *
     * @param subscriberId
     * @return
     */
    public GoodsSpuSelectData listSelectData(String subscriberId) {
        Map<String, Object> whereMap1 = ImmutableMap.of(
                GoodsBrand.Subscriber_id + "=", subscriberId,
                GoodsBrand.If_valid + "=", true);

        Map<String, Object> whereMap2 = ImmutableMap.of(GoodsCategory.If_valid + "=", true);

        Map<String, Object> whereMap3 = ImmutableMap.of(ShopInfo.Subscriber_id + "=", subscriberId);

        return new GoodsSpuSelectData(
                selectListByWhereMap(whereMap1, GoodsBrand.class),
                selectListByWhereMap(whereMap2, GoodsCategory.class),
                selectListByWhereMap(whereMap3, ShopInfo.class)
        );
    }

    /**
     * 商城首页
     *
     * @return
     */
    public Map<String, Object> goodHomeInfo() {
        Map<String, Object> whereMap1 = ImmutableMap.of(
                Article.Article_type + "=", InformationConst.ARTICLE_TYPE_SHOP,
                Article.Content_type + "=", InformationConst.CONTENT_TYPE_GOODS,
                Article.IfValid + "=", true
        );
        List<Article> articles = selectListByWhereMap(whereMap1, Article.class);

        Pagination pagination = new Pagination();
        List<GoodsSpu> goodsSpuList = listGoodsSpu(new GoodsSpu(), pagination);

        return ImmutableMap.of(
                "banner", articles,
                "goodsList1", goodsSpuList,
                "goodsList2", goodsSpuList
        );
    }
}
