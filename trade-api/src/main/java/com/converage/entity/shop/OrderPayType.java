package com.converage.entity.shop;

import lombok.Data;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import static com.converage.constance.SettlementConst.SETTLEMENT_ALI_PAY;
import static com.converage.constance.SettlementConst.SETTLEMENT_CURRENCY;
import static com.converage.constance.SettlementConst.SETTLEMENT_WECHAT_PAY;

@Data
public class OrderPayType {
    private List<String> orderIdList;
    private BigDecimal currencyPrice = BigDecimal.ZERO;
    private BigDecimal integralPrice = BigDecimal.ZERO;
    private BigDecimal cnyPrice = BigDecimal.ZERO;
    private List<OrderSettlement> settlementIds;
    private String orderMergeId;


    public OrderPayType(){
        orderIdList = new ArrayList<>();
        settlementIds = new ArrayList<>();
        settlementIds.add(new OrderSettlement("微信支付", "https://ecbc-common-img.oss-cn-beijing.aliyuncs.com/zhifubao-2.png", SETTLEMENT_WECHAT_PAY, "使用微信支付"));
        settlementIds.add(new OrderSettlement("支付宝支付", "https://ecbc-common-img.oss-cn-beijing.aliyuncs.com/zhifubao-2.png", SETTLEMENT_ALI_PAY, "使用支付宝支付"));
        settlementIds.add(new OrderSettlement("TC支付", "https://ecbc-common-img.oss-cn-beijing.aliyuncs.com/zhifubao-2.png", SETTLEMENT_CURRENCY, "使用TC支付"));
    }
}
