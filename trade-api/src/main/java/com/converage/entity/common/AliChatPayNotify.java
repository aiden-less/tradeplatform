package com.converage.entity.common;

import com.converage.architecture.exception.BusinessException;
import lombok.Data;

import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;

@Data
public class AliChatPayNotify {
    private Date notifyTime;
    private String notifyType;
    private String notifyId;
    private String appId;
    private String charset;
    private String version;
    private String signType;
    private String sign;
    private String tradeNo;
    private String outTradeNo;
    private String sellerId;
    private String tradeStatus;
    private BigDecimal totalAmount;

    public void buildEntityFromRspMap(Map<String, String> params) {
        try {
            this.notifyTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(params.get("notify_time"));
        } catch (ParseException e) {
            throw new BusinessException("日期参数异常");
        }
        this.notifyType = params.get("notify_type");
        this.notifyId = params.get("notify_id");
        this.appId = params.get("app_id");
        this.charset = params.get("charset");
        this.version = params.get("version");
        this.signType = params.get("sign_type");
        this.sign = params.get("sign");
        this.tradeNo = params.get("trade_no");
        this.outTradeNo = params.get("out_trade_no");
        this.sellerId = params.get("seller_id");
        this.tradeStatus = params.get("trade_status");
        this.totalAmount = new BigDecimal(params.get("total_amount"));
    }
}
