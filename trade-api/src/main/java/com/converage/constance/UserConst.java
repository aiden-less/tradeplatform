package com.converage.constance;


public class UserConst {
    //用户状态
    public static final int USER_STATUS_NORMAL = 1;//正常
    public static final int USER_STATUS_FROZEN = 0;//冻结

    //查询邀请记录类型
    public static final int USER_INVITE_TYPE_DIRECT = 1;//直接邀请
    public static final int USER_INVITE_TYPE_INDIRECT = 2;//间接邀请


    //用户实名认证状态
    public static final int USER_CERT_STATUS_NONE = 0;//未认证
    public static final int USER_CERT_STATUS_ING = 1;//认证中
    public static final int USER_CERT_STATUS_PASS = 2;//认证通过
    public static final int USER_CERT_STATUS_UNPASS = 3;//认证不通过

    //短信验证码类型
    public static final int MSG_CODE_TYPE_LOGINANDREGISTER = 1;//注册/登录
    public static final int MSG_CODE_TYPE_INVITEREGISTER = 11;//邀请注册
    public static final int MSG_CODE_TYPE_RESET_LOGINPWD = 2;//重置登录密码
    public static final int MSG_CODE_TYPE_SETTLE_PAYPWD = 3;//设置支付密码
    public static final int MSG_CODE_TYPE_RESET_PAYPWD = 4;//重置支付密码
    public static final int MSG_CODE_TYPE_UNBIND_PHONE = 5;//解绑手机号码
    public static final int MSG_CODE_TYPE_CHANGE_PHONE = 6;//更改手机号码
    public static final int MSG_CODE_TYPE_BIND_BANK = 7;//绑定银行卡
    public static final int MSG_CODE_TYPE_BANK_WITHDRAW = 8;//银行卡提现
    public static final int MSG_CODE_TYPE_BINGD_PHONE = 9;//微信登录绑定手机号码
    public static final int MSG_CODE_TYPE_AUTH = 10;//身份验证

    //提交实名认证图片类型
    public static final String APPLY_CERT_PIC_TYPE_FRONT = "front";//注册
    public static final String APPLY_CERT_PIC_TYPE_BACK = "back";//注册
    public static final String APPLY_CERT_PIC_TYPE_HANDLER = "handler";//注册

    //用户消息类型
    public static final Integer USER_MESSAGE_TYPE_LOGISTICS = 1;//物流
    public static final Integer USER_MESSAGE_TYPE_ACTIVITY = 2;//活动
    public static final Integer USER_MESSAGE_TYPE_SYSTEM = 3;//系统




    //用户类型
    public static final int USER_MERCHANT_TYPE_NONE = 1;//普通用户
    public static final int USER_MERCHANT_TYPE_BEING = 2;//商户用户



    //银行卡提现状态
    /** 待处理 */
    public static final int WITHDRAW_STATE_PENDING = 0;
    /** 通过 */
    public static final int WITHDRAW_STATE_PASSED = 1;
    /** 不通过 */
    public static final int WITHDRAW_STATE_NOTPASS = 2;


    public static String getUserStatus(int status) {
        switch (status) {
            case USER_STATUS_NORMAL :
                return "正常";
            case USER_STATUS_FROZEN :
                return "冻结";
            default:
                return "未知";
        }
    }

    public static String getUserType(int type) {
        switch (type) {
            case USER_MERCHANT_TYPE_NONE :
                return "普通用户";
            case USER_MERCHANT_TYPE_BEING :
                return "会员";
            default:
                return "未知";
        }
    }


    public static String getUserLevel(int level) {
        switch (level) {
            default:
                return "未知";
        }
    }
}
