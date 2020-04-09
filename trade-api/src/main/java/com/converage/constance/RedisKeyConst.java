package com.converage.constance;

import com.converage.entity.shop.OrderInfo;
import com.converage.entity.shop.SampleMachineLocation;

public class RedisKeyConst {
    /** 文章点赞 set 类型*/
    public static final String ARTICLE_LIKE = "article:like:%s";

    /** 文章收藏 set 类型*/
    public static final String ARTICLE_COLLECT = "article:collect:%s";


    //商铺的未支付订单
    public static final String USER_SHOPPINGCART = "shoppingcart:%s";

    //APP访问token (格式 appAccessToken:userId)
    public static final String APP_ACCESS_TOKEN = "appAccessToken:%s";

    public static final String ADMIN_ACCESS_TOKEN = "adminAccessToken:%s";

    /**
     * 一些公共的资源数据
     */
    public static final String COMMON_RESOURCE = "common:resource";

    /**
     * 全部用户的数据(用户id,邀请id)
     */
    public static final String ALL_USER_SAMPLE_INFO = "all_user_sample_info";

    /**
     * 图形验证码key
     */
    public static final String VERTIFY_PICTURE = "vertifyImg";


    public static final String HUOBI = "huobi";
}
