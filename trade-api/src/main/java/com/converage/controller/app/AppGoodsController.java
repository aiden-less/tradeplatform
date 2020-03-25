package com.converage.controller.app;

import com.converage.architecture.dto.Pagination;
import com.converage.architecture.dto.Result;
import com.converage.architecture.utils.JwtUtils;
import com.converage.architecture.utils.ResultUtils;
import com.converage.entity.shop.GoodsCollection;
import com.converage.entity.shop.GoodsDetail;
import com.converage.entity.shop.GoodsSku;
import com.converage.entity.shop.GoodsSpu;
import com.converage.service.shop.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.io.UnsupportedEncodingException;
import java.util.List;

import static com.converage.constance.ShopConst.GOODS_STATUS_INSALE;

@RestController
@RequestMapping("app/goods")
public class AppGoodsController {

    @Autowired
    private GoodsSpuService goodsSpuService;

    @Autowired
    private GoodsSkuService goodsSkuService;

    /**
     * 查询商品
     *
     */
    @RequestMapping("spuList")
    public Result<?> spuList(GoodsSpu goodsSpu) {
        Pagination pagination = goodsSpu.buildPagination();
        goodsSpu.setStatus(GOODS_STATUS_INSALE);
        List<GoodsSpu> goodsSpuList = goodsSpuService.listGoodsSpu(goodsSpu, pagination);
        return ResultUtils.success(goodsSpuList, pagination.getTotalRecordNumber());
    }

    /**
     * 商城首页
     *
     */
    @RequestMapping("home")
    public Result<?> home() {
        return ResultUtils.success(goodsSpuService.goodHomeInfo());
    }

    /**
     * 商品详情
     *
     * @param request
     * @param spuId
     * @return
     * @throws UnsupportedEncodingException
     */
    @RequestMapping("spuDetail/{spuId}")
    public Result<?> spuDetail(HttpServletRequest request, @PathVariable String spuId) throws UnsupportedEncodingException {
        String userId = JwtUtils.getUserByToken(request.getHeader(JwtUtils.ACCESS_TOKEN_NAME)).getId();
        GoodsDetail goodsDetail = goodsSpuService.getGoodsDetail(userId, spuId);
        return ResultUtils.success(goodsDetail);
    }

    /**
     * sku选择
     *
     * @param spuId
     * @param specIdStr
     * @return
     */
    @RequestMapping("skuChoose")
    public Result<?> skuChoose(HttpServletRequest request, String spuId, String specIdStr) {
        GoodsSku goodsSku = goodsSkuService.chooseGoodsSku(spuId, specIdStr);
        return ResultUtils.success(goodsSku);
    }

    /**
     * 收藏商品
     *
     * @param request
     * @param spuId
     * @return
     * @throws UnsupportedEncodingException
     */
    @RequestMapping("collect/{spuId}")
    public Result<?> collect(HttpServletRequest request, @PathVariable String spuId) throws UnsupportedEncodingException {
        String userId = JwtUtils.getUserByToken(request.getHeader(JwtUtils.ACCESS_TOKEN_NAME)).getId();
        goodsSpuService.updateGoodCollection(userId, spuId);
        return ResultUtils.success();
    }

    /**
     * 用户商品收藏列表
     *
     * @param request
     * @param pagination
     * @return
     * @throws UnsupportedEncodingException
     */
    @RequestMapping("collection/{spuType}")
    public Result<?> collection(HttpServletRequest request, @PathVariable Integer spuType, Pagination pagination) throws UnsupportedEncodingException {
        String userId = JwtUtils.getUserByToken(request.getHeader(JwtUtils.ACCESS_TOKEN_NAME)).getId();
        List<GoodsCollection> goodsCollections = goodsSpuService.listGoodsCollection(userId, pagination, spuType);
        return ResultUtils.success(goodsCollections);
    }

    /**
     * 商品对应的卡券列表
     *
     * @param request
     * @param spuId
     * @return
     */
    @RequestMapping("vouchers/{spuId}")
    public Result<?> vouchers(HttpServletRequest request, @PathVariable Integer spuId) {
        String userId = JwtUtils.getUserByToken(request.getHeader(JwtUtils.ACCESS_TOKEN_NAME)).getId();
        return ResultUtils.success();
    }

}
