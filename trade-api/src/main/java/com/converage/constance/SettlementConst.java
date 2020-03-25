package com.converage.constance;

public class SettlementConst {
    public static final int SETTLEMENT_FREE = 0;//免费
    public static final int SETTLEMENT_CURRENCY = 1;//代币
    public static final int SETTLEMENT_STATIC_CURRENCY = 11;//配送品值
    public static final int SETTLEMENT_DYNAMIC_CURRENCY = 12;//赠送品值
    public static final int SETTLEMENT_USDT = 2;//USDT
    public static final int SETTLEMENT_INTEGRAL = 3;//积分
    //    public static final int SETTLEMENT_ORE = 4;//矿石
    public static final int SETTLEMENT_ORE = 4;//矿石
    public static final int SETTLEMENT_COMPUTING_POWER = 8;//算力
    public static final int SETTLEMENT_CANDY = 5;//糖果
    public static final int SETTLEMENT_VOUCHER = 6;//卡券抵扣
    public static final int SETTLEMENT_RMB = 7;//人民币


    public static final int SETTLEMENT_WECHAT_PAY = 101;//微信支付
    public static final int SETTLEMENT_ALI_PAY = 102;//支付宝支付

    //资产 比例类型
    /**
     * 0
     */
    public static final int RATE_TYPE_200 = 200;
    /**
     * (0.6 流通 + 0.4 矿石)
     */
    public static final int RATE_TYPE_201 = 201;
    /**
     * (0.7 流通 + 0.3 矿石)
     */
    public static final int RATE_TYPE_202 = 202;
    /**
     * (0.8 流通 + 0.2 矿石)
     */
    public static final int RATE_TYPE_203 = 203;
    /**
     * (0.9 流通 + 0.1 矿石)
     */
    public static final int RATE_TYPE_204 = 204;


    //资产充提转审核状态
    public static final int USERASSETS_RECHARGE_AUDIT_NONE = 0;//未审核
    public static final int USERASSETS_RECHARGE_AUDIT_PASS = 1;//通过
    public static final int USERASSETS_RECHARGE_AUDIT_UNPASS = 2;//驳回


    public static final int USERASSETS_RECHARGE = 1;    //充值
    public static final int USERASSETS_WITHDRAW = 2;    //提现
    public static final int USERASSETS_TRANSFER = 3;    //转账
}
