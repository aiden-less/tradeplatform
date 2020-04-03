package com.converage.controller.app;

import com.converage.architecture.dto.Pagination;
import com.converage.architecture.dto.Result;
import com.converage.architecture.utils.ResultUtils;
import com.converage.client.RedisClient;
import com.converage.constance.InformationConst;
import com.converage.constance.RedisKeyEnum;
import com.converage.entity.TradePairNews;
import com.converage.entity.information.Article;
import com.converage.entity.market.TradePair;
import com.converage.service.information.ArticleService;
import com.converage.service.transaction.TradePairService;
import com.google.common.collect.ImmutableMap;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;
import java.util.Set;

import static com.converage.constance.RedisKeyEnum.CctHomeTradePairNews;
import static com.converage.constance.RedisKeyEnum.CctRafRate;

/**
 * Created by 旺旺 on 2020/3/24.
 */
@RequestMapping(value = "app")
@RestController
public class AppHomeController {

    @Autowired
    private ArticleService articleService;

    @Autowired
    private TradePairService tradePairService;

    @Autowired
    private RedisClient redisClient;

    /**
     * app主页信息
     *
     * @return
     */
    @RequestMapping("home")
    public Result<?> appHome() {
        Pagination pagination = new Pagination();
        //APP首页公告
        List<Article> announceList = articleService.listArticle(InformationConst.ARTICLE_TYPE_ACTIVITY, new Pagination<>());

        //APP首页广告
        List<Article> advertiseList = articleService.listArticle(InformationConst.ARTICLE_TYPE_ADVERTISE, new Pagination<>());

        //APP首页交易对行情
        List<TradePairNews> advertiseTradePair = tradePairService.listAdvertiseTradePair();

        //涨幅榜
        Set<ZSetOperations.TypedTuple<Object>> raf = redisClient.reverseRangeWithScores(CctRafRate.getKey(), 0, 9);

        Map<String, Object> map = ImmutableMap.of(
                "announceList", announceList,
                "advertiseList", advertiseList,
                "advertiseTradePair", advertiseTradePair,
                "raf", raf
        );

        return ResultUtils.success(map);
    }


    /**
     * web主页信息
     *
     * @return
     */
    @RequestMapping("web")
    public Result<?> web() {

        return ResultUtils.success();
    }


}
