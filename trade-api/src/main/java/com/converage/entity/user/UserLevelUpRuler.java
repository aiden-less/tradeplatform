package com.converage.entity.user;

import com.converage.architecture.mybatis.annotation.Column;
import com.converage.architecture.mybatis.annotation.Id;
import com.converage.architecture.mybatis.annotation.Table;
import org.apache.ibatis.type.Alias;

import java.math.BigDecimal;

/**
 * 用户升级规则
 */
@Alias("UserLevelUpRuler")
@Table(name = "user_level_up_ruler")
public class UserLevelUpRuler {

    @Id
    @Column(name = Id)
    private String id;

    /** 等级名称 */
    @Column(name = Level_name)
    private String levelName;

    /** 等级号 */
    @Column(name = Level_no)
    private Integer levelNo;

    /** 邀请伞下推荐用户需要购买矿机数量 */
    @Column(name = Mining_machine_amount)
    private Integer miningMachineAmount;

    /** 押金金额 */
    @Column(name = Deposit)
    private BigDecimal deposit;

    /** 资产类型 */
    @Column(name = Settlement_id)
    private Integer settlementId;

    public static final String Id = "id";
    public static final String Level_name = "level_name";
    public static final String Level_no = "level_no";
    public static final String Mining_machine_amount = "mining_machine_amount";
    public static final String Deposit = "deposit";
    public static final String Settlement_id = "settlement_id";

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getLevelName() {
        return levelName;
    }

    public void setLevelName(String levelName) {
        this.levelName = levelName;
    }

    public Integer getLevelNo() {
        return levelNo;
    }

    public void setLevelNo(Integer levelNo) {
        this.levelNo = levelNo;
    }

    public Integer getMiningMachineAmount() {
        return miningMachineAmount;
    }

    public void setMiningMachineAmount(Integer miningMachineAmount) {
        this.miningMachineAmount = miningMachineAmount;
    }

    public BigDecimal getDeposit() {
        return deposit;
    }

    public void setDeposit(BigDecimal deposit) {
        this.deposit = deposit;
    }

    public Integer getSettlementId() {
        return settlementId;
    }

    public void setSettlementId(Integer settlementId) {
        this.settlementId = settlementId;
    }
}
