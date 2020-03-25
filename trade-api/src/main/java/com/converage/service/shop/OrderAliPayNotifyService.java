package com.converage.service.shop;

import com.alipay.api.AlipayApiException;
import com.converage.architecture.service.BaseService;
import com.converage.entity.common.AliChatPayNotify;
import com.converage.entity.shop.OrderInfo;
import com.converage.entity.shop.OrderMergeTempInfo;
import com.converage.exception.AliPayNotifyException;
import com.converage.service.common.AliPayService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

import static com.converage.constance.SettlementConst.SETTLEMENT_RMB;

@Service
public class OrderAliPayNotifyService extends BaseService {

    @Autowired
    private AliPayService aliPayService;

    @Autowired
    private OrderService orderService;


    /**
     * ali pay callback for shop order
     */
    public void shoppingNotify(Map<String,List<String>> paramMap) throws AliPayNotifyException {
        try {
            AliChatPayNotify aliChatPayNotify = aliPayService.notifyAnalyse(paramMap);
            OrderMergeTempInfo orderMergeTempInfo = orderService.analyseOrderById(aliChatPayNotify.getOutTradeNo());

            List<OrderInfo> orderInfoList = orderMergeTempInfo.getOrderInfoList();
            String userId = orderMergeTempInfo.getUserId();
            orderService.finishPayOrder(orderInfoList, userId, SETTLEMENT_RMB);
        } catch (AliPayNotifyException | AlipayApiException e) {
            throw new AliPayNotifyException();
        }
    }

}
