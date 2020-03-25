package com.converage.entity.user;

import com.converage.architecture.mybatis.annotation.Column;
import com.converage.architecture.mybatis.annotation.Id;
import com.converage.architecture.mybatis.annotation.Table;
import lombok.Data;
import org.apache.ibatis.type.Alias;

import java.io.Serializable;
import java.math.BigDecimal;
import java.sql.Timestamp;

/**
 * 银行卡提现
 */
@Data
@Alias("BankWithdraw")
@Table(name = "bank_withdraw")
public class BankWithdraw implements Serializable {
    @Id
    @Column(name = Id)
    private String id;

    @Column(name = User_id)
    private String userId;

    @Column(name = Name)
    private String name;

    @Column(name = Number)
    private String number;

    @Column(name = Real_name)
    private String realName;

    @Column(name = Id_card)
    private String idCard;

    @Column(name = Amount)
    private BigDecimal amount;

    @Column(name = State)
    private Integer state;

    @Column(name = Create_time)
    private Timestamp createTime;

    @Column(name = Update_time)
    private Timestamp updateTime;

    //扩展
    private String userName;

    //DB Column name
    public static final String Id = "id";
    public static final String User_id = "user_id";
    public static final String Name = "name";
    public static final String Number = "number";
    public static final String Real_name = "real_name";
    public static final String Id_card = "id_card";
    public static final String Amount = "amount";
    public static final String State = "state";
    public static final String Create_time = "create_time";
    public static final String Update_time = "update_time";
}