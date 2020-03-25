package com.converage.service.common;

import com.converage.architecture.exception.BusinessException;
import com.converage.architecture.service.BaseService;
import com.converage.entity.common.WeChatPayNotify;
import com.converage.exception.WeChatPayNotifyException;
import com.converage.utils.BigDecimalUtils;
import com.converage.utils.WeChatPayCommonUtils;
import org.jdom.JDOMException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.text.DecimalFormat;
import java.time.Instant;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;

@Service
public class WeChatPayService extends BaseService {

    private static String weChatPayAppId;
    private static String weChatMchKey;
    private static String weChatMchId;

    @Autowired
    private GlobalConfigService globalConfigService;

    @PostConstruct
    public void init() {
//        weChatPayAppId = globalConfigService.get(GlobalConfigService.Enum.APP_WEIXIN_APP_ID);
//        weChatMchId = globalConfigService.get(GlobalConfigService.Enum.WECHAT_MCH_ID);
//        weChatMchKey = globalConfigService.get(GlobalConfigService.Enum.WECHAT_MCH_KEY);
    }

    /**
     * 微信预支付
     *
     * @param tradeNo
     * @param orderPrice
     * @param description
     * @param requestIp
     * @param notifyUrl
     * @return
     */
    public Map<String, String> weChatPrePay(String tradeNo, BigDecimal orderPrice, String description, String requestIp, String notifyUrl) {
        SortedMap<String, Object> parameterMap = new TreeMap<>();
        parameterMap.put("appid", weChatPayAppId);  //应用appid
        parameterMap.put("mch_id", weChatMchId);  //商户号
        parameterMap.put("nonce_str", WeChatPayCommonUtils.getRandomString(32));
        parameterMap.put("body", description);
        parameterMap.put("out_trade_no", tradeNo);
        parameterMap.put("fee_type", "CNY");
        DecimalFormat df = new DecimalFormat("0");
        parameterMap.put("total_fee", df.format(BigDecimalUtils.multiply(orderPrice, new BigDecimal(100))));
        parameterMap.put("spbill_create_ip", requestIp);
        parameterMap.put("notify_url", notifyUrl);
        parameterMap.put("trade_type", "APP");
        String serverSign = WeChatPayCommonUtils.createSign("utf-8", parameterMap, weChatMchKey);
        parameterMap.put("sign", serverSign);
        String requestXML = WeChatPayCommonUtils.getRequestXml(parameterMap);

        String result = WeChatPayCommonUtils.httpsRequest("https://api.mch.weixin.qq.com/pay/unifiedorder", "POST", requestXML);
        Map<String, String> resultMap;
        try {
            String timestamp = String.valueOf(Instant.now().getEpochSecond());
            resultMap = WeChatPayCommonUtils.doXMLParse(result);
            checkResult(resultMap);

            SortedMap<String, Object> parameterMap2 = new TreeMap<>();
            parameterMap2.put("appid", resultMap.get("appid"));  //应用appid
            parameterMap2.put("noncestr", resultMap.get("nonce_str"));
            parameterMap2.put("partnerid", resultMap.get("mch_id"));  //商户号
            parameterMap2.put("prepayid", resultMap.get("prepay_id"));
            parameterMap2.put("package", WeChatPayCommonUtils.FIELD_PACKAGE);
            parameterMap2.put("timestamp", timestamp);
            String clientSign = WeChatPayCommonUtils.createSign("utf-8", parameterMap2, weChatMchKey);
            resultMap.put("package", WeChatPayCommonUtils.FIELD_PACKAGE);
            resultMap.put("timestamp", timestamp);
            resultMap.put("sign", clientSign);

        } catch (JDOMException | IOException e) {
            throw new WeChatPayNotifyException("认证异常");
        }
        return resultMap;
    }


    /**
     * 微信支付通知数据解析
     *
     * @param inStream
     * @return
     */
    public WeChatPayNotify notifyAnalyse(InputStream inStream) throws WeChatPayNotifyException {
        try {
            ByteArrayOutputStream outSteam = new ByteArrayOutputStream();
            byte[] buffer = new byte[1024];
            int len;
            while ((len = inStream.read(buffer)) != -1) {
                outSteam.write(buffer, 0, len);
            }
            String resultXml = new String(outSteam.toByteArray(), "utf-8");
            Map<String, String> params = WeChatPayCommonUtils.doXMLParse(resultXml);
            outSteam.close();
            inStream.close();

            if (!WeChatPayCommonUtils.isWechatPaySign(params, weChatMchKey)) {
                throw new WeChatPayNotifyException("认证异常");
            } else {
                String mchId = params.get("mch_id");
                String mchOrderId = params.get("transaction_id");
                String wxOrderId = params.get("out_trade_no");
                String wxAppId = params.get("appid");
                String openId = params.get("openid");
                BigDecimal orderPrice = BigDecimalUtils.multiply(new BigDecimal(params.get("total_fee")), new BigDecimal(100));
                Timestamp createTime = new Timestamp(System.currentTimeMillis());
                return new WeChatPayNotify(mchId, mchOrderId, wxOrderId, wxAppId, openId, orderPrice, createTime);
            }
        } catch (IOException | JDOMException e) {
            throw new WeChatPayNotifyException("认证异常");
        }
    }


    /**
     * 判断微信支付结果
     *
     * @param resultMap
     */
    private void checkResult(Map<String, String> resultMap) {
        String returnCode = resultMap.get("return_code");
        if (returnCode.equals("SUCCESS")) {
            String resultCode = resultMap.get("result_code");
            if (resultCode.equals("FAIL")) {
                throw new BusinessException(resultMap.get("err_code_des"));
            }
        } else {
            throw new BusinessException(resultMap.get("return_msg"));
        }

    }


}
