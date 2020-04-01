package com.converage.service.shop;

import com.alibaba.fastjson.JSONArray;
import com.converage.architecture.dto.Pagination;
import com.converage.architecture.exception.BusinessException;
import com.converage.architecture.service.BaseService;
import com.converage.client.RedisClient;
import com.converage.constance.*;
import com.converage.controller.app.req.ShopOrderPayReq;
import com.converage.entity.user.*;
import com.converage.mapper.shop.GoodsSkuMapper;
import com.converage.mapper.shop.GoodsSpuMapper;
import com.converage.service.common.AliPayService;
import com.converage.service.common.GlobalConfigService;
import com.converage.service.common.WeChatPayService;
import com.converage.utils.*;
import com.converage.controller.app.req.ShopOrderSkuReq;
import com.converage.entity.shop.*;
import com.converage.mapper.shop.OrderMapper;
import com.converage.service.user.AssetsTurnoverService;
import com.converage.service.user.UserAssetsService;
import com.google.common.collect.ImmutableMap;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionCallbackWithoutResult;
import org.springframework.transaction.support.TransactionTemplate;

import javax.servlet.http.HttpServletRequest;
import java.math.BigDecimal;
import java.util.*;

import static com.converage.constance.AssetTurnoverConst.*;
import static com.converage.constance.SettlementConst.*;
import static com.converage.constance.SettlementConst.SETTLEMENT_CURRENCY;
import static com.converage.constance.ShopConst.*;
import static com.converage.constance.UserConst.USER_MESSAGE_TYPE_LOGISTICS;

@Service
public class OrderService extends BaseService {

    private static final Logger logger = LoggerFactory.getLogger(OrderService.class);

    @Autowired
    private TransactionTemplate transactionTemplate;

    @Autowired
    private UserAssetsService userAssetsService;

    @Autowired
    private AssetsTurnoverService assetsTurnoverService;

    @Autowired
    private GoodsSpecService goodsSpecService;

    @Autowired
    private OrderMapper orderMapper;

    @Autowired
    private WeChatPayService weChatPayService;

    @Autowired
    private AliPayService aliPayService;

    @Autowired
    private RedisClient redisClient;

    @Autowired
    private GoodsSpuService goodsSpuService;

    @Autowired
    private GoodsSpuMapper goodsSpuMapper;

    @Autowired
    private GoodsSkuMapper goodsSkuMapper;

    @Autowired
    private GlobalConfigService globalConfigService;

    public OrderPayType createOrder(User user, ShopOrderPayReq shopOrderPayReq, Integer orderType) {
        String userId = user.getId();
        user = selectOneById(userId, User.class);

        List<String> shoppingCartIds = shopOrderPayReq.getShoppingCartIdList();
        String redisKey = String.format(RedisKeyConst.USER_SHOPPINGCART, userId);
        List<ShopOrderSkuReq> shopOrderSkuReqList = new ArrayList<>();
        if (shoppingCartIds != null) {
            for (String shoppingCartId : shoppingCartIds) {
                ShoppingCart shoppingCartItem = (ShoppingCart) redisClient.getHashKey(redisKey, shoppingCartId);
                shopOrderSkuReqList.add(new ShopOrderSkuReq(shoppingCartItem));
            }
            shopOrderPayReq.setOrderSkuReqList(shopOrderSkuReqList);
        }

        return shoppingOrder(shopOrderPayReq, user, orderType);
    }

    private OrderPayType shoppingOrder(ShopOrderPayReq shopOrderPayReq, User user, Integer orderType) throws BusinessException {
        List<ShopOrderSkuReq> orderSkuReqList = shopOrderPayReq.getOrderSkuReqList();
        Integer settlementId = shopOrderPayReq.getSettlementId();
        String addressId = shopOrderPayReq.getAddressId();
        String userId = user.getId();

        ValueCheckUtils.notEmpty(addressId, "请选择收货地址");
        ShoppingAddress shoppingAddress = selectOneById(addressId, ShoppingAddress.class);
        ValueCheckUtils.notEmpty(shoppingAddress, "未找到收货地址记录");

        OrderPayType orderPayType = new OrderPayType();
        Map<String, List<OrderItem>> shopOrderItemMap = new HashMap<>();
        List<String> shopCartIdList = new ArrayList<>();

        for (ShopOrderSkuReq orderSkuReq : orderSkuReqList) {
            String spuId = orderSkuReq.getSpuId();
            String shopCartId = orderSkuReq.getShoppingCartId();
            Integer buyNumber = orderSkuReq.getNumber();

            //validate goods status
            GoodsSpu goodsSpu = goodsSpuService.validateGoodsSpu(spuId);

            //sort specStr
            String specIdStr = goodsSpecService.sortSpecIdStr(orderSkuReq.getSpecIdStr());
            GoodsTempInfo goodsTempInfo = buildGoodsSTempInfo(buyNumber, goodsSpu, specIdStr);

            //build orderItemMap on shopId
            //key:shopId,value:orderItemList
            buildShopOrderItemMap(shopOrderItemMap, goodsTempInfo, buyNumber);

            shopCartIdList.add(shopCartId);
        }

        //decrease goods stock -> finish order create
        transactionTemplate.execute(new TransactionCallbackWithoutResult() {
            @Override
            protected void doInTransactionWithoutResult(TransactionStatus transactionStatus) {
                for (Map.Entry<String, List<OrderItem>> entry : shopOrderItemMap.entrySet()) {
                    finishCreateOrder(orderPayType, entry, settlementId, user, shoppingAddress, shopCartIdList, orderType);
                }
                String orderIds = JSONArray.toJSONString(orderPayType.getOrderIdList());
                OrderMergeInfo orderMergeInfo = new OrderMergeInfo(userId, orderIds, orderPayType.getCnyPrice());
                ValueCheckUtils.notZero(insertIfNotNull(orderMergeInfo), "创建订单失败");
                orderPayType.setOrderMergeId(orderMergeInfo.getId());
            }
        });
        return orderPayType;
    }

    public Map<String, String> prePayOrder(HttpServletRequest request, String userId, ShopOrderPayReq shopOrderPayReq) {
        Integer settlementId = shopOrderPayReq.getSettlementId();
        String payPassword = shopOrderPayReq.getPayPassword();
        OrderMergeTempInfo orderMergeTempInfo = buildOrderMergeTmpInfo(shopOrderPayReq);

        List<String> orderIdList = orderMergeTempInfo.getOrderIdList();
        String orderTargetId = orderMergeTempInfo.getOrderTargetId();
        BigDecimal orderCnyPrice = orderMergeTempInfo.getOrderCnyPrice();

        Map<String, String> resultMap = null;
        String domain = globalConfigService.get(GlobalConfigService.Enum.APP_DOMAIN_NAME);

        switch (settlementId) {
            case SETTLEMENT_DYNAMIC_CURRENCY:
                User user = selectOneById(userId, User.class);
                List<OrderInfo> orderInfoList = orderMapper.listUserOrderByIds(userId, orderIdList);
                ValueCheckUtils.notEmpty(orderInfoList, "未找到订单");

                if (StringUtils.isEmpty(user.getPayPassword())) {
                    throw new BusinessException("请先设置支付密码");
                }
                if (!user.getIfFreePayPwd()) {
                    if (!EncryptUtils.md5Password(payPassword).equals(user.getPayPassword())) {
                        throw new BusinessException("支付密码错误");
                    }
                }
                finishPayOrder(orderInfoList, userId, SETTLEMENT_DYNAMIC_CURRENCY);
                break;

            case SETTLEMENT_WECHAT_PAY:
                resultMap = weChatPayService.weChatPrePay(
                        orderTargetId, orderCnyPrice, "TASTE商城微信支付", WeChatPayCommonUtils.getRemoteHost(request), domain + WeChatPayShopOrderNotifyURL
                );
                break;

            case SETTLEMENT_ALI_PAY:
                resultMap = aliPayService.aliPrePay(
                        orderTargetId, orderCnyPrice, "TASTE商城支付宝支付", domain + AliPayShopOrderNotifyURL
                );
                break;

            default:
                throw new BusinessException("错误支付参数");
        }
        return resultMap;
    }


    void finishPayOrder(List<OrderInfo> orderInfoList, String userId, Integer settlementId) {
        String errorMsg = "订单支付失败";
        transactionTemplate.execute(new TransactionCallbackWithoutResult() {
            @Override
            protected void doInTransactionWithoutResult(TransactionStatus transactionStatus) {
                for (OrderInfo orderInfo : orderInfoList) {
                    String orderId = orderInfo.getId();
                    BigDecimal orderPrice = getPriceFromSettlementId(orderInfo, settlementId);
                    ValueCheckUtils.notZero(orderMapper.updateOrder4Pay(userId, orderId, settlementId, orderPrice, ORDER_STATUS_NONE_SEND), errorMsg);
                    ValueCheckUtils.notZero(orderMapper.updateOrderItem4Pay(orderId, orderPrice), errorMsg);
                    String detailStr = "商城购物支付,订单号为:" + orderInfo.getOrderNo();
//                    ValueCheckUtils.notZero(userAssetsService.decreaseUserAssets(userId, orderPrice, settlementId), "用户资产不足");
                    assetsTurnoverService.createAssetsTurnover(userId, TURNOVER_TYPE_GOODS, orderPrice, userId, COMPANY_ID, settlementId, detailStr);
                }
            }
        });
    }

    private GoodsTempInfo buildGoodsSTempInfo(Integer buyNumber, GoodsSpu goodsSpu, String specIdStr) {
        String spuId = goodsSpu.getId();
        String skuOrSpuId;
        BigDecimal currencyPrice;
        BigDecimal integralPrice;
        BigDecimal cnyPrice;
        BigDecimal currencyReward;

        String stockNoneMsg = "商品暂无库存";
        if (StringUtils.isNotBlank(specIdStr)) {//has sku
            Map<String, Object> skuWhereMap = ImmutableMap.of(GoodsSku.Spu_id + " = ", spuId, GoodsSku.Spec_json + " = ", specIdStr);
            GoodsSku goodsSku = selectOneByWhereMap(skuWhereMap, GoodsSku.class);
            ValueCheckUtils.notEmpty(goodsSku, stockNoneMsg);
            if (buyNumber > goodsSku.getStock()) {
                throw new BusinessException(stockNoneMsg);
            }
            skuOrSpuId = goodsSku.getId();
            currencyPrice = goodsSku.getCurrencyPrice();
            cnyPrice = goodsSku.getCnyPrice();
            currencyReward = goodsSku.getCurrencyReward();
            integralPrice = goodsSku.getIntegralPrice();
        } else {//haven't sku
            if (buyNumber > goodsSpu.getStock()) {
                throw new BusinessException(stockNoneMsg);
            }
            skuOrSpuId = spuId;
            currencyPrice = goodsSpu.getCurrencyPrice();
            cnyPrice = goodsSpu.getCnyPrice();
            currencyReward = goodsSpu.getCurrencyReward();
            integralPrice = goodsSpu.getIntegralPrice();
        }
        return new GoodsTempInfo(skuOrSpuId, currencyPrice, integralPrice, cnyPrice, currencyReward, goodsSpu);
    }

    private void buildShopOrderItemMap(Map<String, List<OrderItem>> shopOrderItemMap, GoodsTempInfo goodsTempInfo, Integer buyNumber) {
        String shopId = goodsTempInfo.getShopId();

        List<OrderItem> orderItemsList;
        if (shopOrderItemMap.containsKey(shopId)) {
            orderItemsList = shopOrderItemMap.get(shopId);
            orderItemsList.add(new OrderItem(goodsTempInfo, buyNumber));
        } else {
            orderItemsList = new ArrayList<>();
            orderItemsList.add(new OrderItem(goodsTempInfo, buyNumber));
            shopOrderItemMap.put(shopId, orderItemsList);
        }
    }

    private void finishCreateOrder(OrderPayType orderPayType, Map.Entry<String, List<OrderItem>> entry, Integer settlementId,
                                   User user, ShoppingAddress shoppingAddress, List<String> shopCartIdList, Integer orderType) {
        BigDecimal orderCurrencyPrice = BigDecimal.ZERO;
        BigDecimal orderIntegralPrice = BigDecimal.ZERO;
        BigDecimal orderCnyPrice = BigDecimal.ZERO;

        String userId = user.getId();
        String userName = user.getUserName();

        String orderErrorMsg = "创建订单失败";
        String stockErrorMsg = "商品库存不足";

        String shopId = entry.getKey();
        List<OrderItem> orderItemList = entry.getValue();

        for (OrderItem orderItem : orderItemList) {
            BigDecimal buyNum = BigDecimal.valueOf(orderItem.getNumber());
            orderCurrencyPrice = BigDecimalUtils.add(orderCurrencyPrice, orderItem.getCurrencyPrice().multiply(buyNum));
            orderIntegralPrice = BigDecimalUtils.add(orderIntegralPrice, orderItem.getIntegralPrice().multiply(buyNum));
            orderCnyPrice = BigDecimalUtils.add(orderCnyPrice, orderItem.getCnyPrice().multiply(buyNum));
        }

        OrderInfo orderInfo = new OrderInfo();
        orderInfo.buildOrderInfo(
                settlementId, userId, shopId, userName, orderCurrencyPrice, orderIntegralPrice, orderCnyPrice,
                orderType, ORDER_STATUS_NOT_PAY, shoppingAddress
        );

        ValueCheckUtils.notZero(insertIfNotNull(orderInfo), orderErrorMsg);
        orderPayType.getOrderIdList().add(orderInfo.getId());
        orderPayType.setCurrencyPrice(orderCurrencyPrice);
        orderPayType.setIntegralPrice(orderIntegralPrice);
        orderPayType.setCnyPrice(orderCnyPrice);

        for (OrderItem orderItem : orderItemList) {
            orderItem.setOrderId(orderInfo.getId());
            if (orderItem.getIfSku()) {
                ValueCheckUtils.notZero(goodsSkuMapper.decreaseSkuStock(orderItem.getGoodsSkuId(), orderItem.getNumber()), stockErrorMsg);
            } else {
                ValueCheckUtils.notZero(goodsSpuMapper.decreaseSpuStock(orderItem.getGoodsSpuId(), orderItem.getNumber()), stockErrorMsg);
            }
        }
        ValueCheckUtils.notZero(insertBatch(orderItemList, false), orderErrorMsg);

        for (String shopCartId : shopCartIdList) {
            String redisKey = String.format(RedisKeyConst.USER_SHOPPINGCART, userId);
            if (StringUtils.isNotEmpty(shopCartId)) {
                redisClient.delete(redisKey, shopCartId);
            }
        }
    }

    public List<OrderGoodsInfo> listAppOrderGoodsInfo(String userId, Integer orderStatus, Integer orderType, Pagination pagination) {
        List<OrderGoodsInfo> orderGoodsInfoList = orderMapper.listUserOrderGoods(userId, orderStatus, orderType, pagination);
        for (OrderGoodsInfo orderGoodsInfo : orderGoodsInfoList) {//订单列表
            Integer goodsNumber = 0;
            for (OrderItem orderItem : orderGoodsInfo.getOrderItemList()) {//订单商品列表
                orderItem.setSpecStr(goodsSpecService.strSpecIdToSpecValue(orderItem.getSpecJson()));
                goodsNumber += orderItem.getNumber();
            }
            orderGoodsInfo.setGoodsNumber(goodsNumber);
        }

        return orderGoodsInfoList;
    }

    public OrderGoodsInfo getAppOrderGoodsInfo(String userId, String orderId) {
        OrderGoodsInfo orderGoodsInfo = orderMapper.getUserOrderGoodsInfo(userId, orderId);
        Integer goodsNumber = 0;
        for (OrderItem orderItem : orderGoodsInfo.getOrderItemList()) {//订单商品列表
            orderItem.setSpecStr(goodsSpecService.strSpecIdToSpecValue(orderItem.getSpecJson()));
            goodsNumber += orderItem.getNumber();
        }
        orderGoodsInfo.setGoodsNumber(goodsNumber);
        return orderGoodsInfo;
    }


    public OrderPayType orderPayType(String userId, ShopOrderPayReq shopOrderPayReq) {
        shopOrderPayReq.setIfMerge(false);
        OrderMergeTempInfo orderMergeTempInfo = buildOrderMergeTmpInfo(shopOrderPayReq);
        List<String> orderIdList = orderMergeTempInfo.getOrderIdList();
        List<OrderInfo> orderInfoList = orderMapper.listUserOrderByIds(userId, orderIdList);
        ValueCheckUtils.notZero(orderInfoList.size(), "未找到订单");

        OrderPayType orderPayType = new OrderPayType();
        for (OrderInfo orderInfo : orderInfoList) {
            orderPayType.getOrderIdList().add(orderInfo.getId());
            orderPayType.setCurrencyPrice(orderPayType.getCurrencyPrice().add(orderInfo.getCurrencyPrice()));
            orderPayType.setCnyPrice(orderPayType.getCnyPrice().add(orderInfo.getCnyPrice()));
        }

        orderPayType.setOrderMergeId(shopOrderPayReq.getOrderMergeId());

        return orderPayType;
    }

    public List<OrderGoodsInfo> listAdminOrderGoodsInfo(OrderInfo orderQueryInfo, Pagination pagination) {
        List<OrderGoodsInfo> orderGoodsInfoList = orderMapper.listAdminOrderGoodsInfo(
                orderQueryInfo.getOrderNo(), orderQueryInfo.getUserName(), orderQueryInfo.getSettlementId(), orderQueryInfo.getLogisticNumber(),
                orderQueryInfo.getStartTime(), orderQueryInfo.getEndTime(), orderQueryInfo.getStatus(), pagination
        );
        for (OrderGoodsInfo orderGoodsInfo : orderGoodsInfoList) {
            for (OrderItem orderItem : orderGoodsInfo.getOrderItemList()) {
                orderItem.setSpecStr(goodsSpecService.strSpecIdToSpecValue(orderItem.getSpecJson()));
            }
        }
        return orderGoodsInfoList;
    }


    public void updateOrderStatus(String orderId, Integer status, String statusStr) {
        OrderInfo orderInfo = new OrderInfo();
        orderInfo.setId(orderId);
        orderInfo.setStatus(status);
        orderInfo.setStatusStr(statusStr);
        if (updateIfNotNull(orderInfo) == 0) {
            throw new BusinessException("更新订单状态失败");
        }
    }

    public void receive(String userId, String orderId) {
        Map<String, Object> whereMap = ImmutableMap.of(OrderInfo.User_id + "=", userId, OrderInfo.Id + "=", orderId);
        OrderInfo orderInfo = selectOneByWhereMap(whereMap, OrderInfo.class);
        ValueCheckUtils.notEmpty(orderInfo, "未找到订单信息");

        Integer orderType = orderInfo.getOrderType();
        ValueCheckUtils.notEmpty(orderType, "订单类型异常");
        List<OrderItem> orderItemList = selectListByWhereString(OrderItem.Order_id + "=", orderId, OrderItem.class);
        ValueCheckUtils.notEmpty(orderItemList, "未找到订单商品");
        BigDecimal currencyReward = orderItemList.stream().map(OrderItem::getCurrencyReward).reduce(BigDecimal.ZERO, BigDecimal::add);
        transactionTemplate.execute(new TransactionCallbackWithoutResult() {
            @Override
            protected void doInTransactionWithoutResult(TransactionStatus transactionStatus) {
                if (currencyReward.compareTo(BigDecimal.ZERO) > 0) {
//                    userAssetsService.increaseUserAssets(userId, currencyReward, SETTLEMENT_CURRENCY);
                    assetsTurnoverService.createAssetsTurnover(
                            userId, TURNOVER_TYPE_GOODS, currencyReward, COMPANY_ID, userId, SETTLEMENT_CURRENCY, "商城购物商品品值奖励"
                    );
                }
                updateOrderStatus(orderId, ShopConst.ORDER_STATUS_RECEIVE, "已完成");
                orderMapper.updateOrderItemsStatus(orderId, ShopConst.ORDER_STATUS_RECEIVE);
            }
        });
    }

    @Transactional
    public void refunds(String userId, String orderItemId, String refundsReason) {
        ValueCheckUtils.notEmpty(orderItemId, "请选择订单商品");

        OrderItem orderItem = selectOneById(orderItemId, OrderItem.class);
        ValueCheckUtils.notEmpty(orderItem, "未找到订单商品");
        Map<String, Object> whereMap = ImmutableMap.of(OrderInfo.Id + "=", orderItem.getOrderId(), OrderInfo.User_id + "=", userId);
        OrderInfo orderInfo = selectOneByWhereMap(whereMap, OrderInfo.class);
        ValueCheckUtils.notEmpty(orderInfo, "未找到订单信息");
        ValueCheckUtils.notEmpty(orderItem, "未找到订单商品信息");

        orderItem.setStatus(ShopConst.ORDER_STATUS_RETURN_APPLY);
        orderItem.setRefundsReason(refundsReason);
        orderInfo.setStatus(ShopConst.ORDER_STATUS_RETURN_APPLY);
        orderInfo.setStatusStr("退货中");

        String errorMsg = "申请退货失败";
        ValueCheckUtils.notZero(updateIfNotNull(orderItem), errorMsg);
        ValueCheckUtils.notZero(updateIfNotNull(orderInfo), errorMsg);


    }

    public void cancelOrder(String userId, String orderId, String cancelReason) {
        ValueCheckUtils.notEmpty(orderId, "请选择订单");
        Map<String, Object> orderWhereMap = ImmutableMap.of(OrderInfo.Id + "=", orderId, OrderInfo.User_id + "=", userId);
        OrderInfo orderInfo = selectOneByWhereMap(orderWhereMap, OrderInfo.class);

        Map<String, Object> orderItemWhereMap = ImmutableMap.of(OrderItem.Order_id + "=", orderId);
        List<OrderItem> orderItemList = selectListByWhereMap(orderItemWhereMap, OrderItem.class);

        ValueCheckUtils.notEmpty(orderId, "未找到订单");
        orderInfo.setStatus(ShopConst.ORDER_STATUS_CANCEL);
        orderInfo.setStatusStr("已取消");
        orderInfo.setCancelReason(cancelReason);
        String errorMsg = "取消订单失败";

        transactionTemplate.execute(new TransactionCallbackWithoutResult() {
            @Override
            protected void doInTransactionWithoutResult(TransactionStatus transactionStatus) {
                ValueCheckUtils.notZero(updateIfNotNull(orderInfo), errorMsg);
                ValueCheckUtils.notZero(orderMapper.updateOrderItemsStatus(orderId, ShopConst.ORDER_STATUS_CANCEL), errorMsg);
                restoreGoodsStock(orderItemList);
            }
        });
    }

    public void restoreGoodsStock(List<OrderItem> orderItems) {
        if (orderItems == null || orderItems.size() == 0) {
            return;
        }
        List<String> orderIds = new ArrayList<>();
        List<OrderItem> spuOrderItem = new ArrayList<>();
        List<OrderItem> skuOrderItem = new ArrayList<>();
        orderItems.forEach(orderItem -> {
            if (StringUtils.isNotEmpty(orderItem.getGoodsSkuId())) {
                skuOrderItem.add(orderItem);
            } else {
                spuOrderItem.add(orderItem);
            }

            String orderId = orderItem.getOrderId();
            if (!orderIds.contains(orderId)) {
                orderIds.add(orderId);
            }
        });
        orderMapper.updateOrderListStatus(orderIds, ShopConst.ORDER_STATUS_CANCEL);

        if (spuOrderItem.size() > 0) {
            for (OrderItem orderItem : spuOrderItem) {
                goodsSpuMapper.restoreStock(orderItem);
            }

        }

        if (skuOrderItem.size() > 0) {
            for (OrderItem orderItem : skuOrderItem) {
                goodsSkuMapper.restoreStock(orderItem);
            }

        }
    }

    public void updateOrderLogisticNumber(String orderId, String logisticNumber) {
        OrderInfo orderInfo = selectOneById(orderId, OrderInfo.class);
        String userId = orderInfo.getUserId();
        List<OrderItem> orderItemList = selectListByWhereString(OrderItem.Order_id + "=", orderId, OrderItem.class);
        OrderItem orderItem = orderItemList.get(0);
        String imgUrl = orderItem.getImgUrl();
        Integer orderStatus = orderInfo.getStatus();
        if (orderStatus != ORDER_STATUS_NONE_SEND) {
            throw new BusinessException("该订单已发货");
        }
        orderInfo.setLogisticNumber(logisticNumber);
        orderInfo.setStatus(ShopConst.ORDER_STATUS_DELIVERY);
        orderInfo.setStatusStr("待收货");
        String errorMsg = "更新订单物流号失败";
        transactionTemplate.execute(new TransactionCallbackWithoutResult() {
            @Override
            protected void doInTransactionWithoutResult(TransactionStatus transactionStatus) {
                ValueCheckUtils.notZero(updateIfNotNull(orderInfo), errorMsg);
                ValueCheckUtils.notZero(orderMapper.updateOrderItemsStatus(orderId, ShopConst.ORDER_STATUS_DELIVERY), errorMsg);
                UserMessage userMessage = new UserMessage(
                        userId, "订单已发货", USER_MESSAGE_TYPE_LOGISTICS, orderItem.getGoodsDescription(), "订单编号" + orderInfo.getOrderNo(), imgUrl
                );
                insertIfNotNull(userMessage);

                //TODO 物流消息推送

            }
        });

    }

    private BigDecimal getPriceFromSettlementId(OrderInfo orderInfo, Integer settlementId) {
        switch (settlementId) {
            case SETTLEMENT_CURRENCY:
                return orderInfo.getCurrencyPrice();

            case SETTLEMENT_INTEGRAL:
                return orderInfo.getIntegralPrice();

            case SETTLEMENT_RMB:
                return orderInfo.getCnyPrice();

            default:
                throw new BusinessException("支付类型参数有误");
        }
    }

    //build orderTempInfo if order belong to merge order
    private OrderMergeTempInfo buildOrderMergeTmpInfo(ShopOrderPayReq shopOrderPayReq) {
        String errorMsg = "订单id异常";
        List<String> orderIdList;
        String orderTargetId;
        BigDecimal orderCnyPrice;
        String orderMergeId = shopOrderPayReq.getOrderMergeId();
        Boolean ifMerge = shopOrderPayReq.getIfMerge();
        ValueCheckUtils.notEmpty(orderMergeId, "订单ID缺省");
        ValueCheckUtils.notEmpty(ifMerge, "订单组合类型缺省");
        if (shopOrderPayReq.getIfMerge()) {
            orderTargetId = MERGE_ORDER + orderMergeId;
            OrderMergeInfo orderMergeInfo = selectOneById(orderMergeId, OrderMergeInfo.class);
            ValueCheckUtils.notEmpty(orderMergeInfo, errorMsg);
            orderCnyPrice = orderMergeInfo.getOrderPrice();
            orderIdList = JSONArray.parseArray(orderMergeInfo.getOrderIds(), String.class);
        } else if (!shopOrderPayReq.getIfMerge()) {
            orderTargetId = COMMON_ORDER + orderMergeId;
            OrderInfo orderInfo = selectOneById(orderMergeId, OrderInfo.class);
            ValueCheckUtils.notEmpty(orderInfo, errorMsg);
            orderCnyPrice = orderInfo.getCnyPrice();
            orderIdList = Arrays.asList(orderInfo.getId());
        } else {
            throw new BusinessException(errorMsg);
        }
        return new OrderMergeTempInfo(orderIdList, orderTargetId, orderCnyPrice);
    }


    //analyse order if belong to merge order by order id tag
    public OrderMergeTempInfo analyseOrderById(String orderId) {
        List<OrderInfo> orderInfoList;
        ValueCheckUtils.notEmpty(orderId, "订单id为空");

        List<String> orderIdList;
        String userId;
        if (orderId.contains(MERGE_ORDER)) {
            OrderMergeInfo orderMergeInfo = selectOneById(orderId.replace(MERGE_ORDER, ""), OrderMergeInfo.class);
            userId = orderMergeInfo.getUserId();
            orderIdList = JSONArray.parseArray(orderMergeInfo.getOrderIds(), String.class);
        } else if (orderId.contains(COMMON_ORDER)) {
            OrderInfo orderInfo = selectOneById(orderId.replace(COMMON_ORDER, ""), OrderInfo.class);
            userId = orderInfo.getUserId();
            orderIdList = Arrays.asList(orderInfo.getId());
        } else {
            throw new BusinessException("订单id：" + orderId + "异常");
        }
        orderInfoList = orderMapper.listOrderByIds(orderIdList);

        return new OrderMergeTempInfo(userId, orderInfoList);
    }

}
