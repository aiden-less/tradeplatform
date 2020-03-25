package com.converage.service.shop;

import com.converage.architecture.service.BaseService;
import com.converage.entity.common.WeChatPayNotify;
import com.converage.entity.shop.OrderInfo;
import com.converage.entity.shop.OrderMergeTempInfo;
import com.converage.exception.WeChatPayNotifyException;
import com.converage.mapper.shop.OrderMapper;
import com.converage.service.common.WeChatPayService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.InputStream;
import java.util.List;

import static com.converage.constance.SettlementConst.SETTLEMENT_RMB;


@Service
public class OrderWechatPayNotifyService extends BaseService {

    @Autowired
    private WeChatPayService weChatPayService;

    @Autowired
    private OrderService orderService;

    @Autowired
    private OrderMapper orderMapper;

    /**
     * wechat pay callback for shop order
     */
    public void shoppingNotify(InputStream inputStream) {
        try {
            WeChatPayNotify weChatPayNotify = weChatPayService.notifyAnalyse(inputStream);
            OrderMergeTempInfo orderMergeTempInfo = orderService.analyseOrderById(weChatPayNotify.getWxOrderId());

            String userId = orderMergeTempInfo.getUserId();
            List<OrderInfo> orderInfoList = orderMergeTempInfo.getOrderInfoList();
            orderService.finishPayOrder(orderInfoList, userId, SETTLEMENT_RMB);
        } catch (Exception e) {
            throw new WeChatPayNotifyException();
        }


    }

}
