package com.converage.entity.user;

import com.converage.architecture.mybatis.annotation.Column;
import com.converage.architecture.mybatis.annotation.Id;
import com.converage.architecture.mybatis.annotation.Table;
import lombok.Data;
import org.apache.ibatis.type.Alias;

import java.io.Serializable;
import java.math.BigDecimal;
import java.sql.Timestamp;

@Data
@Alias("InviteUserRecord")
@Table(name = "invite_user_record")
public class InviteUserRecord implements Serializable {

    private static final long serialVersionUID = 2120887352497993703L;

    @Id
    @Column(name = Id)
    private String id;

    @Column(name = User_id)
    private String userId; //被邀请 用户id

    @Column(name = Invite_user_id)
    private String inviteUserId; //所 邀请用户id

    @Column(name = Invite_type)
    private Integer inviteType; //邀请类型

    @Column(name = Reward_value)
    private BigDecimal rewardValue; //奖励值

    @Column(name = Reward_score)
    private BigDecimal rewardScore; //贡献分

    @Column(name = Register_time)
    private Timestamp registerTime; //注册时间

    private String userAccount;
    private String headImgUrl;
    private String inviteLevel;
    private String rewardExplain;
    private Integer socialNum;
    private Integer packageNum;
    private BigDecimal socialAchievement;
    private String registerTimeStr;

    //DB Column name
    public static final String Id = "id";
    public static final String User_id = "user_id";
    public static final String Invite_user_id = "invite_user_id";
    public static final String Invite_type = "invite_type";
    public static final String Reward_value = "reward_value";
    public static final String Reward_score = "reward_score";
    public static final String Register_time = "register_time";

    public InviteUserRecord(){}

    public InviteUserRecord(String userId, String inviteUserId, Integer inviteType, BigDecimal rewardValue, Timestamp registerTime) {
        this.userId = userId;
        this.inviteUserId = inviteUserId;
        this.inviteType = inviteType;
        this.rewardValue = rewardValue;
        this.registerTime = registerTime;
    }

    public InviteUserRecord(String userId, String inviteUserId, Integer inviteType) {
        this.userId = userId;
        this.inviteUserId = inviteUserId;
        this.inviteType = inviteType;
    }

}
