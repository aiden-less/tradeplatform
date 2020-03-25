package com.converage.entity.shop;

import com.converage.architecture.dto.Pagination;
import com.converage.architecture.mybatis.annotation.Column;
import com.converage.architecture.mybatis.annotation.Id;
import com.converage.architecture.mybatis.annotation.Table;
import com.converage.constance.SettlementConst;
import com.converage.constance.ShopConst;
import lombok.Data;
import org.apache.ibatis.type.Alias;

import java.io.Serializable;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.List;

@Data
@Alias("GoodsSpu")
@Table(name = "goods_spu")//商品spu表
public class GoodsSpu implements Serializable {
    private static final long serialVersionUID = -4987682283871933822L;

    @Id
    @Column(name = Id)
    private String id;

    @Column(name = Spu_no)
    private String spuNo;//spu编号

    @Column(name = Goods_name)
    private String goodsName;//商品名称

    @Column(name = Goods_description)
    private String goodsDescription;//商品描述

    @Column(name = Attribute)
    private String attribute;//商品属性

    @Column(name = Package_type)
    private Integer packageType;//礼包类型

    @Column(name = Freight)
    private BigDecimal freight;//运费

    @Column(name = Currency_price)
    private BigDecimal currencyPrice;//平台币价格

    @Column(name = Integral_price)
    private BigDecimal integralPrice;//积分价格

    @Column(name = Usdt_price)
    private BigDecimal usdtPrice;//USDT价格

    @Column(name = Cny_price)
    private BigDecimal cnyPrice;//人民币价格

    @Column(name = Currency_reward)
    private BigDecimal currencyReward; //平台比奖励

    @Column(name = Stock)
    private Integer stock;//库存

    @Column(name = Settlement_id)
    private Integer settlementId;//支付方式id

    @Column(name = Category_id)
    private String categoryId;//分类id（goods_category表id）

    @Column(name = Brand_id)
    private String brandId;//品牌id（goods_brand表id）

    @Column(name = Shop_id)
    private String shopId;//商铺id

    @Column(name = Default_img_url)
    private String defaultImgUrl;//spu 默认图片

    @Column(name = Spec_img_url)
    private String specImgUrl;//spu 规格图片

    @Column(name = Create_time)
    private Timestamp createTime;

    @Column(name = Update_time)
    private Timestamp updateTime;

    //ShopConst.SPU_TYPE_*
    @Column(name = Spu_type)
    private Integer spuType;//spu类型

    //ShopConst.GOODS_STATUS_*
    @Column(name = Status)
    private Integer status;//sku状态

    @Column(name = If_valid)
    private Boolean ifValid;//是否有效

    @Column(name = If_sku)
    private Boolean ifSku;//是否有sku



    //扩展属性
    //spuId
    private String spuId;
    //类目名
    private String categoryName;
    //品牌名
    private String brandName;
    //分页对象
    private Pagination pagination;
    //分页页码
    private Integer pageNum;
    //分页条目数
    private Integer pageSize;
    //所有图片集合
    private List<GoodsImg> allImg;
    //默认图对象
    private List<GoodsImg> defaultImg;
    //规格图对象
    private List<GoodsImg> specImg;
    //详情图url
    private List<String> detailImgUrl;
    //详情图对象列表
    private List<GoodsImg> detailImg;
    //介绍图url
    private List<String> introduceImgUrl;
    //介绍图对象列表
    private List<GoodsImg> introduceImg;
    //条件查询开始时间
    private Timestamp startTime;
    //条件查询结束时间
    private Timestamp endTime;
    //规格名Id
    private List<String> specNameIdList;
    private Integer countCollection;    //收藏数
    private Integer countBuy;    //购买数
    private String integralStr = "TCNY";
    private String priceIcon = "https://taste-common-img.oss-cn-hongkong.aliyuncs.com/2019-07-05/aa67a886-7465-48bc-9894-05e7d5f63d3c.png";    //价格图标

    //DB Column name
    public static final String Id = "id";
    public static final String Spu_no = "spu_no";
    public static final String Attribute = "attribute";
    public static final String Package_type = "package_type";
    public static final String Goods_name = "goods_name";
    public static final String Goods_description = "goods_description";
    public static final String Freight = "freight";
    public static final String Currency_price = "currency_price";
    public static final String Integral_price = "integral_price";
    public static final String Usdt_price = "usdt_price";
    public static final String Cny_price = "cny_price";
    public static final String Currency_reward = "currency_reward";
    public static final String Stock = "stock";
    public static final String Settlement_id = "settlement_id";
    public static final String Category_id = "category_id";
    public static final String Brand_id = "brand_id";
    public static final String Shop_id = "shop_id";
    public static final String Default_img_url = "default_img_url";
    public static final String Spec_img_url = "spec_img_url";
    public static final String Create_time = "create_time";
    public static final String Update_time = "update_time";
    public static final String Spu_type = "spu_type";
    public static final String Status = "status";
    public static final String If_valid = "if_valid";
    public static final String If_sku = "If_sku";


    public Pagination buildPagination() {
        this.pageNum = this.pageNum == null ? 0 : this.pageNum;
        this.pageSize = this.pageSize == null ? 10 : this.pageSize;
        Pagination pagination = new Pagination();
        pagination.setPageNum(this.pageNum);
        pagination.setPageSize(this.pageSize);
        return pagination;
    }

    public static GoodsSpu buildBeautyGoodsSpu(Beauty beauty) {
        GoodsSpu goodsSpu = new GoodsSpu();
        goodsSpu.setGoodsName(beauty.getName());
        goodsSpu.setGoodsDescription(beauty.getSpecification());
        goodsSpu.setDefaultImgUrl(beauty.getImage_home());
        goodsSpu.setSettlementId(SettlementConst.SETTLEMENT_FREE);
        goodsSpu.setFreight(BigDecimal.ZERO);
        goodsSpu.setSpuType(ShopConst.SPU_TYPE_BEAUTY);
        goodsSpu.setIfValid(false);

        return goodsSpu;
    }
}
