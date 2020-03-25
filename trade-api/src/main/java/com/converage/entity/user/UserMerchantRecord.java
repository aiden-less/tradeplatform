package com.converage.entity.user;


import com.converage.architecture.mybatis.annotation.Column;
import com.converage.architecture.mybatis.annotation.Id;
import lombok.Data;
import org.apache.ibatis.type.Alias;

import java.io.Serializable;
import java.sql.Timestamp;

@Data
@Alias("UserMerchantRecord")
//@Table(name = "user_merchant_record")
public class UserMerchantRecord implements Serializable{
    private static final long serialVersionUID = -5541708179006779737L;

    @Id
    @Column(name = Id)
    public String id;

    @Column(name = User_id)
    public String userId;

    @Column(name = Merchant_name)
    public String merchantRankName;//

    @Column(name = Count_machine)
    public Integer countMachine;

    @Column(name = Merchant_rule_id)
    public String merchantRulerId;

    @Column(name = Create_time)
    public Timestamp createTime;

    @Column(name = Expire_time)
    public Timestamp expireTime;

    @Column(name = If_valid)
    public Boolean ifValid;


    //DB Column name
    public static final String Id = "id";
    public static final String User_id = "user_id";
    public static final String Merchant_name = "merchant_name";
    public static final String Count_machine = "count_machine";
    public static final String Merchant_rule_id = "merchant_ruler_id";
    public static final String Create_time = "create_time";
    public static final String Expire_time = "expire_time";
    public static final String If_valid = "if_valid";

    public UserMerchantRecord(){}

    public UserMerchantRecord(String userId, String merchantRankName,Integer countMachine, String merchantRulerId, Timestamp createTimestamp, Timestamp expireTimestamp, Boolean ifValid) {
        this.userId = userId;
        this.merchantRankName = merchantRankName;
        this.countMachine = countMachine;
        this.merchantRulerId = merchantRulerId;
        this.createTime = createTimestamp;
        this.expireTime = expireTimestamp;
        this.ifValid = ifValid;
    }
}
