package com.converage.controller.app;

import com.alibaba.fastjson.JSONObject;
import com.converage.architecture.dto.Pagination;
import com.converage.architecture.dto.Result;
import com.converage.architecture.utils.JwtUtils;
import com.converage.architecture.utils.ResultUtils;
import com.converage.constance.SettlementConst;
import com.converage.controller.app.req.ShopOrderPayReq;
import com.converage.entity.shop.OrderGoodsInfo;
import com.converage.entity.user.User;
import com.converage.service.common.RSAService;
import com.converage.service.shop.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.io.UnsupportedEncodingException;
import java.util.List;

import static com.converage.constance.CommonConst.REQUST_TIME_OUT_SECOND;
import static com.converage.constance.ShopConst.ORDER_TYPE_GOODS;
import static com.converage.constance.ShopConst.ORDER_TYPE_PACK;

@RequestMapping(value = "/app/order")
@RestController
public class AppOrderController {

    @Autowired
    private OrderService orderService;


    @Autowired
    private RSAService rsaService;

    /**
     * 按状态查询订单
     *
     * @param request
     * @param orderStatus
     * @return
     */
    @RequestMapping("list")
    public Result<?> listByOrderStatus(HttpServletRequest request, Integer orderStatus, Integer orderType, Pagination pagination) throws UnsupportedEncodingException {
        String userId = JwtUtils.getUserByToken(request.getHeader(JwtUtils.ACCESS_TOKEN_NAME)).getId();
        List<OrderGoodsInfo> orderInfoList = orderService.listAppOrderGoodsInfo(userId, orderStatus, orderType, pagination);
        return ResultUtils.success(orderInfoList);
    }


    /**
     * 订单详情
     *
     * @param request
     * @param orderId
     * @return
     */
    @RequestMapping("detail/{orderId}")
    public Result<?> detail(HttpServletRequest request, @PathVariable("orderId") String orderId) {
        String userId = JwtUtils.getUserByToken(request.getHeader(JwtUtils.ACCESS_TOKEN_NAME)).getId();
        OrderGoodsInfo orderGoodsInfo = orderService.getAppOrderGoodsInfo(userId, orderId);
        return ResultUtils.success(orderGoodsInfo);
    }


    /**
     * 订单创建
     *
     * @param request
     * @param shopOrderPayReq
     * @return
     */
    @RequestMapping("goods/create")
    public Object create4Goods(HttpServletRequest request, @RequestBody ShopOrderPayReq shopOrderPayReq) throws UnsupportedEncodingException {
        User user = JwtUtils.getUserByToken(request.getHeader(JwtUtils.ACCESS_TOKEN_NAME));
        return ResultUtils.success(orderService.createOrder(user, shopOrderPayReq, ORDER_TYPE_GOODS));
    }

    /**
     * 礼包订单创建
     *
     * @param request
     * @param shopOrderPayReq
     * @return
     */
    @RequestMapping("pack/create")
    public Object create4Pack(HttpServletRequest request, @RequestBody ShopOrderPayReq shopOrderPayReq) throws UnsupportedEncodingException {
        User user = JwtUtils.getUserByToken(request.getHeader(JwtUtils.ACCESS_TOKEN_NAME));
        return ResultUtils.success(orderService.createOrder(user, shopOrderPayReq, ORDER_TYPE_PACK));
    }


    /**
     * 订单支付类型
     *
     * @param request
     * @return
     */
    @RequestMapping("payType")
    public Object payType(HttpServletRequest request, @RequestBody ShopOrderPayReq shopOrderPayReq) throws UnsupportedEncodingException {
        User user = JwtUtils.getUserByToken(request.getHeader(JwtUtils.ACCESS_TOKEN_NAME));
        return ResultUtils.success(orderService.orderPayType(user.getId(), shopOrderPayReq));
    }

    /**
     * 订单支付
     *
     * @param request
     * @param paramStr
     * @return
     */
    @RequestMapping("pay")
    public Object pay(HttpServletRequest request, String desKeyStr, String paramStr) throws Exception {
        String userId = JwtUtils.getUserByToken(request.getHeader(JwtUtils.ACCESS_TOKEN_NAME)).getId();

        ShopOrderPayReq shopOrderPayReq = JSONObject.parseObject(rsaService.decryptParam(desKeyStr, paramStr), ShopOrderPayReq.class);
        rsaService.checkRequestTimeout(shopOrderPayReq, REQUST_TIME_OUT_SECOND);

        return ResultUtils.success(
                orderService.prePayOrder(request, userId, shopOrderPayReq)
        );
    }


    /**
     * 确认收货
     *
     * @param request
     * @param orderId
     * @return
     */
    @RequestMapping("receive/{orderId}")
    public Result<?> receive(HttpServletRequest request, @PathVariable("orderId") String orderId) {
        String userId = JwtUtils.getUserByToken(request.getHeader(JwtUtils.ACCESS_TOKEN_NAME)).getId();
        orderService.receive(userId, orderId);
        return ResultUtils.success();
    }

    /**
     * 申请退货
     *
     * @param request
     * @param orderItemId
     * @return
     */
    @RequestMapping("refunds/{orderItemId}")
    public Result<?> refunds(HttpServletRequest request, @PathVariable("orderItemId") String orderItemId, String refundsReason) {
        String userId = JwtUtils.getUserByToken(request.getHeader(JwtUtils.ACCESS_TOKEN_NAME)).getId();
        orderService.refunds(userId, orderItemId, refundsReason);
        return ResultUtils.success();
    }

    /**
     * 取消订单
     *
     * @param request
     * @param orderItemId
     * @return
     */
    @RequestMapping("cancel/{orderId}")
    public Result<?> cancel(HttpServletRequest request, @PathVariable("orderId") String orderItemId, String cancelReason) {
        String userId = JwtUtils.getUserByToken(request.getHeader(JwtUtils.ACCESS_TOKEN_NAME)).getId();
        orderService.cancelOrder(userId, orderItemId, cancelReason);
        return ResultUtils.success();
    }


    @RequestMapping("/machine/confirm")
    public Result<?> confirmMachine(HttpServletRequest request, String spuId) throws Exception {
        String userId = JwtUtils.getUserByToken(request.getHeader(JwtUtils.ACCESS_TOKEN_NAME)).getId();
        return ResultUtils.success();
    }

    /**
     * 购买矿机
     *
     * @param request
     * @return
     */
    @PostMapping("/machine/buy")
    public Result<?> buyMachine(HttpServletRequest request, String desKeyStr, String paramStr) throws Exception {
        String userId = JwtUtils.getUserByToken(request.getHeader(JwtUtils.ACCESS_TOKEN_NAME)).getId();

        ShopOrderPayReq shopOrderPayReq = JSONObject.parseObject(rsaService.decryptParam(desKeyStr, paramStr), ShopOrderPayReq.class);
        rsaService.checkRequestTimeout(shopOrderPayReq, REQUST_TIME_OUT_SECOND);

        Integer settlementId = SettlementConst.SETTLEMENT_CURRENCY;
        String spuId = shopOrderPayReq.getSpuId();
        Integer buyNum = shopOrderPayReq.getBuyNum();
        String payPassword = shopOrderPayReq.getPayPassword();


        return ResultUtils.success("购买成功");
    }

    /**
     * 购买道具
     *
     * @param request
     * @return
     */
    @RequestMapping("quotas/buy")
    public Result<?> buyQuotas(HttpServletRequest request, String quotasId, Integer settlementId, Integer rateType, Integer buyNum) {
        String userId = JwtUtils.getUserByToken(request.getHeader(JwtUtils.ACCESS_TOKEN_NAME)).getId();
        return ResultUtils.success("购买成功");
    }

    /**
     * 购买资产包
     *
     * @param request
     * @return
     */
    @RequestMapping("assetsPack/buy")
    public Result<?> buyAssetsPack(HttpServletRequest request, String assetsPackId, Integer settlementId, Integer buyNum) {
        String userId = JwtUtils.getUserByToken(request.getHeader(JwtUtils.ACCESS_TOKEN_NAME)).getId();
        return ResultUtils.success();
    }

}
