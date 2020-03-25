package com.converage.controller.app.req;

import com.converage.entity.encrypt.EncryptEntity;
import lombok.Data;

import java.util.List;

@Data
public class ShopOrderPayReq extends EncryptEntity {
    private String qrCodeStr;//二维码数据字符串
    private String spuId;//spuId
    private String orderMergeId;//主订单id
    private String orderId;//子订单id
    private String voucherId;//卡券Id
    private Integer orderType;//订单类型
    private String addressId; //收货地址id
    private Integer settlementId; //支付方式
    private String payPassword;//支付密码
    private Boolean ifMerge;//是否属于合并订单
    private Integer buyNum;//购买数量
    private List<ShopOrderSkuReq> orderSkuReqList; //sku 信息
    private String phoneNumber;
    private List<String> shoppingCartIdList; //购物车id
}
