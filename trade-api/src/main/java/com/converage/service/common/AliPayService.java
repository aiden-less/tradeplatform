package com.converage.service.common;

import com.alibaba.fastjson.JSONArray;
import com.alipay.api.AlipayApiException;
import com.alipay.api.AlipayClient;
import com.alipay.api.DefaultAlipayClient;
import com.alipay.api.domain.AlipayTradeAppPayModel;
import com.alipay.api.internal.util.AlipaySignature;
import com.alipay.api.request.AlipayTradeAppPayRequest;
import com.alipay.api.response.AlipayTradeAppPayResponse;
import com.google.common.collect.ImmutableMap;
import com.converage.architecture.service.BaseService;
import com.converage.entity.common.AliChatPayNotify;
import com.converage.entity.shop.OrderMergeInfo;
import com.converage.exception.AliPayNotifyException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.converage.constance.CommonConst.UTF_8;
import static com.converage.constance.ShopConst.COMMON_ORDER;
import static com.converage.constance.ShopConst.MERGE_ORDER;

@Service
public class AliPayService extends BaseService {

    private static String aliPayAccountId;
    private static String aliPayAppId;
    private static String aliPayPublicKey;
    private static String aliPayPrivateKey;
    private static String aliPayRSAPublicKey;

    @Autowired
    private GlobalConfigService globalConfigService;

    @PostConstruct
    public void init() {
//        aliPayAccountId = globalConfigService.get(GlobalConfigService.Enum.ALI_PAY_ACCOUNT_ID);
//        aliPayAppId = globalConfigService.get(GlobalConfigService.Enum.ALI_PAY_APP_ID);
//        aliPayPublicKey = globalConfigService.get(GlobalConfigService.Enum.ALI_PAY_PUBLIC_KEY);
//        aliPayPrivateKey = globalConfigService.get(GlobalConfigService.Enum.ALI_PAY_PRIVATE_KEY);
//        aliPayRSAPublicKey = globalConfigService.get(GlobalConfigService.Enum.ALI_PAY_RSA_PUBLIC_KEY);
    }

    public Map<String, String> aliPrePay(String tradeNo, BigDecimal orderPrice, String description, String notifyUrl) {
        System.out.println(notifyUrl);
        AlipayClient alipayClient = new DefaultAlipayClient("https://openapi.alipay.com/gateway.do", aliPayAppId, aliPayPrivateKey, "json", UTF_8, aliPayPublicKey, "RSA2");
        AlipayTradeAppPayRequest request = new AlipayTradeAppPayRequest();
        AlipayTradeAppPayModel model = new AlipayTradeAppPayModel();
        model.setBody(description);
        model.setSubject("Taste商城支付");
        model.setOutTradeNo(tradeNo);
        model.setTimeoutExpress("30m");
        model.setTotalAmount(orderPrice.stripTrailingZeros().toPlainString());
        model.setProductCode("QUICK_MSECURITY_PAY");
        request.setBizModel(model);
        request.setNotifyUrl(notifyUrl);
        try {
            //这里和普通的接口调用不同，使用的是sdkExecute
            AlipayTradeAppPayResponse response = alipayClient.sdkExecute(request);
            return ImmutableMap.of("aliPrepayString", response.getBody());
        } catch (AlipayApiException e) {
            e.printStackTrace();
        }
        return null;
    }


    public AliChatPayNotify notifyAnalyse(Map<String, List<String>> requestParams) throws AlipayApiException {
        Map<String, String> params = new HashMap<>();
        System.out.println("==========================");
        System.out.println(JSONArray.toJSONString(requestParams));
        for (Object o : requestParams.keySet()) {
            String name = (String) o;
            List<String> values = requestParams.get(name);
            String valueStr = values.get(0);
            //乱码解决，这段代码在出现乱码时使用。
            //valueStr = new String(valueStr.getBytes("ISO-8859-1"), "utf-8");
            params.put(name, valueStr);
        }

        AliChatPayNotify aliChatPayNotify = new AliChatPayNotify();
        aliChatPayNotify.buildEntityFromRspMap(params);

        String outTradeNo = aliChatPayNotify.getOutTradeNo();
        String orderId;
        if (outTradeNo.contains(MERGE_ORDER)) {
            orderId = outTradeNo.replace(MERGE_ORDER, "");
        } else {
            orderId = outTradeNo.replace(COMMON_ORDER, "");
        }
        OrderMergeInfo orderMergeInfo = selectOneById(orderId, OrderMergeInfo.class);

        String errorMsg = "支付宝通知参数有误";
        if (orderMergeInfo == null || orderMergeInfo.getOrderPrice().compareTo(aliChatPayNotify.getTotalAmount()) != 0
                || !aliPayAccountId.equals(aliChatPayNotify.getSellerId())
                || !aliPayAppId.equals(aliChatPayNotify.getAppId())) {
            throw new AliPayNotifyException(errorMsg);
        }


        //切记alipaypublickey是支付宝的公钥，请去open.alipay.com对应应用下查看。
        //boolean AlipaySignature.rsaCheckV1(Map<String, String> params, String publicKey, String charset, String sign_type)
        boolean flag = AlipaySignature.rsaCheckV1(params, aliPayRSAPublicKey, UTF_8, "RSA2");
        if (!flag) {
            throw new AlipayApiException("参数异常");
        }


        return aliChatPayNotify;
    }

}
