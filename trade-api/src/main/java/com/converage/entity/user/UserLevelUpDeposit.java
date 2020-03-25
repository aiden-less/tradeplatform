package com.converage.entity.user;

import com.converage.architecture.mybatis.annotation.Column;
import com.converage.architecture.mybatis.annotation.Id;
import com.converage.architecture.mybatis.annotation.Table;
import org.apache.ibatis.type.Alias;

import java.math.BigDecimal;
import java.sql.Timestamp;

/**
 * 用户升级押金表
 */
@Alias("UserLevelUpDeposit")
@Table(name = "user_level_up_deposit")
public class UserLevelUpDeposit {

    @Id
    @Column(name = Id)
    private String id;

    /**
     * 用户ID
     */
    @Column(name = User_id)
    private String userId;

    /**
     * 数目
     */
    @Column(name = Amount)
    private BigDecimal amount;

    /**
     * 资产类型
     */
    @Column(name = Settlement_id)
    private Integer settlementId;

    /**
     * 过期时间
     */
    @Column(name = Expire_time)
    private Timestamp expireTime;

    /**
     * 创建时间
     */
    @Column(name = Create_time)
    private BigDecimal createTime;


    public static final String Id = "id";
    public static final String User_id = "user_id";
    public static final String Amount = "amount";
    public static final String Settlement_id = "settlement_id";
    public static final String Expire_time = "expire_time";
    public static final String Create_time = "create_time";

    public UserLevelUpDeposit(String userId, BigDecimal amount, Integer settlementId, Timestamp expireTime) {
        this.userId = userId;
        this.amount = amount;
        this.settlementId = settlementId;
        this.expireTime = expireTime;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public Integer getSettlementId() {
        return settlementId;
    }

    public void setSettlementId(Integer settlementId) {
        this.settlementId = settlementId;
    }

    public Timestamp getExpireTime() {
        return expireTime;
    }

    public void setExpireTime(Timestamp expireTime) {
        this.expireTime = expireTime;
    }

    public BigDecimal getCreateTime() {
        return createTime;
    }

    public void setCreateTime(BigDecimal createTime) {
        this.createTime = createTime;
    }
}
