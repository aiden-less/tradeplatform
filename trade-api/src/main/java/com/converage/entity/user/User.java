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
@Alias("User")
@Table(name = "user_info")
public class User extends EncryptEntity implements Serializable { //用户信息

    private static final long serialVersionUID = 368896404002697116L;
    @Id
    @Column(name = Id)
    public String id;

    @Column(name = Head_picture_url)
    private String headPictureUrl; //头像图片url

    @Column(name = User_name)
    private String userName; //用户昵称

    @Column(name = User_account)
    private String userAccount; //账号

    @Column(name = Password)
    private String password; //密码

    @Column(name = Pay_password)
    private String payPassword;//支付密码

    @Column(name = Phone_number)
    private String phoneNumber;//手机号

    @Column(name = Invite_id)
    private String inviteId;//注册邀请人id

    @Column(name = Leader_invite_id)
    private String leaderInviteId;//团队邀请人id

    @Column(name = Invite_code)
    private String inviteCode;//邀请码

    @Column(name = Create_time)
    private Timestamp createTime;//注册时间

    @Column(name = Status)
    private Integer status;//状态     //UserConst.USER_STATUS_*

    @Column(name = Type)
    private Integer type;//类型

    @Column(name = Level)
    private Integer level;//用户等级

    @Column(name = Leader_level)
    private Integer leaderLevel;//团队长等级

    @Column(name = Wx_open_id)
    private String wxOpenId;//微信OpenId

    @Column(name = Wx_nickname)
    private String wxNickname;//微信昵称

    @Column(name = Member_type)
    private Integer memberType;//会员类型

    @Column(name = If_free_pay_pwd)
    private Boolean ifFreePayPwd; //是否免密支付

    @Column(name = If_can_otc)
    private Boolean ifCanOtc; //是否可以OTC交易

    @Column(name = If_can_recharge)
    private Boolean ifCanRecharge; //是否可以充值

    @Column(name = If_can_withdraw)
    private Boolean ifCanWithdraw; //是否可以提现

    @Column(name = If_can_transfer)
    private Boolean ifCanTransfer; //是否可以转账

    @Column(name = Compute_reward)
    private BigDecimal computeReward; //邀请算力奖励

    //扩展属性
    private String userId;
    private String accessToken; //访问token
    private String msgCode; //短信验证码
    private String picCode; //图形验证码
    private String countryCode; //国家代码
    private Integer msgType; //UserConst.MSG_CODE_TYPE_* 短信验证码类型
    private List<User> directUserList; //直接邀请用户记录
    private List<User> inDirectUserList; //间接邀请用户记录
    private String newPassword; //新密码
    private String oldPassword; //旧密码
    private Integer updatePwdType; //UserConst.UPDATE_PWD_TYPE_* 修改密码类型
    private Boolean ifRegister;//是否已注册
    private Boolean ifSettlePayPwd;//是否有设置支付密码
    private Boolean ifSettleInviteCode;//是否有邀请码
    private BigDecimal computePower; //算力
    private BigDecimal currency; //代币
    private BigDecimal staticCurrency; //静态资产
    private BigDecimal dynamicCurrency; //动态资产
    private BigDecimal usdt; //usdt
    private BigDecimal integral; //integral
    private BigDecimal freeOre; //可用矿石
    private BigDecimal frozenOre; //冻结矿石
    private BigDecimal quotaOre; //总矿石量
    private Integer certStatus; //认证状态
    private Boolean ifUpgrade; //是否可以升级
    private String mainNetAddr; //地址
    private String certFailMsg;

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
    public static final String Leader_invite_id = "leader_invite_id";
    public static final String Invite_code = "invite_code";
    public static final String Create_time = "create_time";
    public static final String Status = "status";
    public static final String Type = "type";
    public static final String Level = "level";
    public static final String Leader_level = "leader_level";
    public static final String Leader_currency_rate = "leader_currency_rate";
    public static final String Leader_ore_rate = "leader_ore_rate";
    public static final String Wx_open_id = "wx_open_id";
    public static final String Wx_nickname = "wx_nickname";
    public static final String Member_type = "member_type";
    public static final String If_free_pay_pwd = "if_free_pay_pwd";
    public static final String If_can_otc = "if_can_otc";
    public static final String If_can_recharge = "if_can_recharge";
    public static final String If_can_withdraw = "if_can_withdraw";
    public static final String If_can_transfer = "if_can_transfer";
    public static final String Compute_reward = "compute_reward";



    public User(List<User> directUserList, List<User> inDirectUserList) {
        this.directUserList = directUserList;
        this.inDirectUserList = inDirectUserList;
    }

    public User() {

    }

}
