package com.converage.entity.user;

import com.converage.architecture.mybatis.annotation.Column;
import com.converage.architecture.mybatis.annotation.Id;
import com.converage.architecture.mybatis.annotation.Table;
import com.converage.entity.encrypt.EncryptEntity;
import lombok.Data;
import org.apache.ibatis.type.Alias;

import java.io.Serializable;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.List;

@Data
@Table(name = "user_info")
public class User extends EncryptEntity implements Serializable { //用户信息

    private static final long serialVersionUID = 368896404002697116L;

    @Id
    @Column(name = Id)
    public String id;

    @Column(name = User_account)
    private String userAccount; //账号

    @Column(name = Password)
    private String password; //密码

    @Column(name = Pay_password)
    private String payPassword;//支付密码

    @Column(name = Phone_number)
    private String phoneNumber;//手机号

    @Column(name = Invite_id)
    private String inviteId;//邀请人id

    @Column(name = Invite_code)
    private String inviteCode;//邀请码

    @Column(name = Create_time)
    private Timestamp createTime;//注册时间

    @Column(name = Status)
    private Integer status;//状态     //UserConst.USER_STATUS_*

    @Column(name = If_free_pay_pwd)
    private Boolean ifFreePayPwd; //是否免密支付

    @Column(name = If_can_otc)
    private Boolean ifCanOtc; //是否可以交易

    @Column(name = If_can_recharge)
    private Boolean ifCanRecharge; //是否可以充值

    @Column(name = If_can_withdraw)
    private Boolean ifCanWithdraw; //是否可以提现

    @Column(name = If_can_transfer)
    private Boolean ifCanTransfer; //是否可以转账

    //扩展属性
    private String accessToken; //访问token
    private String msgCode; //短信验证码
    private String picCode; //图形验证码
    private Integer msgType; //UserConst.MSG_CODE_TYPE_* 短信验证码类型


    //DB Column name
    public static final String Id = "id";
    public static final String User_id = "user_id";
    public static final String Head_picture_url = "head_picture_url";
    public static final String User_name = "user_name";
    public static final String User_account = "user_account";
    public static final String Password = "password";
    public static final String Pay_password = "pay_password";
    public static final String Phone_number = "phone_number";
    public static final String Invite_id = "invite_id";
    public static final String Invite_code = "invite_code";
    public static final String Create_time = "create_time";
    public static final String Status = "status";
    public static final String If_free_pay_pwd = "if_free_pay_pwd";
    public static final String If_can_otc = "if_can_otc";
    public static final String If_can_recharge = "if_can_recharge";
    public static final String If_can_withdraw = "if_can_withdraw";
    public static final String If_can_transfer = "if_can_transfer";


    public User() {

    }

}
