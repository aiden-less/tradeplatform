package com.converage.constance;

public class ShopConst {
    public static final String SHOP_OFFICIAL = "0";//    //自营店铺id

    //spu类型
    public static final int SPU_TYPE_GOODS = 1;//商城商品
    public static final int SPU_TYPE_BEAUTY = 2;//美妆商品
    public static final int SPU_TYPE_PACKAGE = 3;//小样/生日礼包商品

    //商品状态
    public static final int GOODS_STATUS_OUTSALE = 0;//下架
    public static final int GOODS_STATUS_INSALE = 1;//上架
    public static final int GOODS_STATUS_FROZEN = 99;//冻结


    //商品操作类型
    public static final int OPERATOR_TYPE_INSERT = 1;//新增
    public static final int OPERATOR_TYPE_DELETE = 2;//删除
    public static final int OPERATOR_TYPE_UPDATE = 3;//更新
    public static final int OPERATOR_TYPE_QUERY_LIST = 4;//查询列表
    public static final int OPERATOR_TYPE_QUERY_DETAIL = 5;//查询单个
    public static final int OPERATOR_TYPE_FROZEN = 6;//冻结/解冻
    public static final int OPERATOR_TYPE_SHELF = 7;//上架/下架
    public static final int OPERATOR_TYPE_QUERY_CANCEL = 8;//取消

    //商品图片类型
    public static final int GOODS_IMG_TYPE_DEFAULT = 0;//商品默认图
    public static final int GOODS_IMG_TYPE_INTRODUCE = 1;//商品介绍图
    public static final int GOODS_IMG_TYPE_DETAIL = 2;//商品详情图

    //收货地址操作类型
    public static final int SHOPPING_ADDRESS_OPERATOR_TYPE_INSERT = 1;//新增
    public static final int SHOPPING_ADDRESS_OPERATOR_TYPE_DELETE = 2;//删除
    public static final int SHOPPING_ADDRESS_OPERATOR_TYPE_UPDATE = 3;//更新
    public static final int SHOPPING_ADDRESS_OPERATOR_TYPE_QUERY = 4;//查询
    public static final int SHOPPING_ADDRESS_OPERATOR_TYPE_DEFAULT = 5;//设置默认

    //订单状态
    public static final int ORDER_STATUS_NONE_SEND = 0;//待发货
    public static final int ORDER_STATUS_DELIVERY = 1;//待收货
    public static final int ORDER_STATUS_RECEIVE = 2;//已完成
    public static final int ORDER_STATUS_CANCEL = 3;//已取消
    public static final int ORDER_STATUS_RETURN_APPLY = 4;//申请退货
    public static final int ORDER_STATUS_RETURN_SUCCESS = 40;//退货成功
    public static final int ORDER_STATUS_RETURN_FAIL = 41;//退货失败
    public static final int ORDER_STATUS_NOT_PAY = 99;//待支付

    //订单类型
    public static final int ORDER_TYPE_GOODS = 1;//实物
    public static final int ORDER_TYPE_PACK = 2;//资产包
    public static final int ORDER_TYPE_SCAN_BEAUTY = 3;//线下扫码领取小样
    public static final int ORDER_TYPE_EXCHANGE_BEAUTY = 4;//线上兑换小样礼包
    public static final int ORDER_TYPE_BECOME_MEMBER = 5;//办理年卡会员
    public static final int ORDER_TYPE_BUY_BEAUTY_PACKAGE_VOUCHER = 6;//购买卡券


    //卡券类型
    public static final int VOUCHER_TYPE_DEDUCTION = 1; //抵扣券
    public static final int VOUCHER_TYPE_BEAUTY_PACKAGE_DEDUCTION = 11; //小样礼包抵扣券
    public static final int VOUCHER_TYPE_BIRTH_PACKAGE_DEDUCTION = 12; //生日礼包抵扣券
    public static final int VOUCHER_TYPE_PACKAGE_DISCOUNT = 2; //折扣券


    public static final String VOUCHER_ABLEDATALIST_KEY = "ableDataList"; //可用卡券json key
    public static final String VOUCHER_UNABLEDATALIST_KEY = "unableDataList";//不可用卡券json key

    //卡券状态
    public static final int VOUCHER_STATUS_UNUSER = 0; //未使用
    public static final int VOUCHER_STATUS_USERd = 1; //已使用


    public static final String WeChatPayNotifyController = "/weChatPayNotify"; //微信支付通知Controller路径

    public static final String WeChatPayShopOrderNotify = "/shopOrderNotify";
    public static final String WeChatPayShopOrderNotifyURL = WeChatPayNotifyController + WeChatPayShopOrderNotify;


    public static final String AliPayNotifyController = "/aliPayNotify"; //支付宝支付通知Controller路径
    public static final String AliPayShopOrderNotify = "/shopOrderNotify";
    public static final String AliPayShopOrderNotifyURL = AliPayNotifyController + AliPayShopOrderNotify;

    public static final int TRANSACTION_BUY = 1;  //内盘买入
    public static final int TRANSACTION_SELL = 2;  //内盘卖出


    public static final String MERGE_ORDER = "mergeOrder:";
    public static final String COMMON_ORDER = "commonOrder:";
}
