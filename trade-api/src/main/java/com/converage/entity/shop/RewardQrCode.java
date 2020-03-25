package com.converage.entity.shop;

import com.converage.architecture.mybatis.annotation.Column;
import com.converage.architecture.mybatis.annotation.Id;
import com.converage.architecture.mybatis.annotation.Table;
import lombok.Data;
import org.apache.ibatis.type.Alias;

import java.io.Serializable;
import java.math.BigDecimal;
import java.sql.Timestamp;

@Data
@Alias("RewardQrCode")
@Table(name = "reward_qrcode")//订单详情表
public class RewardQrCode implements Serializable {
    private static final long serialVersionUID = -8247913647739828869L;

    @Id
    @Column(name = Id)
    private String id;

    @Column(name = Batch_no)
    private String batchNo;

    @Column(name = Reward_level)
    private Integer rewardLevel;

    @Column(name = Reward_amount)
    private BigDecimal rewardAmount;

    @Column(name = If_download)
    private Boolean ifDownload;

    @Column(name = If_valid)
    private Boolean ifValid;

    @Column(name = Create_time)
    private Timestamp createTime;

    //DB Column name
    public static final String Id = "id";
    public static final String Batch_no = "batch_no";
    public static final String Reward_level = "reward_level";
    public static final String Reward_amount = "reward_amount";
    public static final String If_download = "if_download";
    public static final String If_valid = "if_valid";
    public static final String Create_time = "create_time";

    public RewardQrCode(){

    }

    public RewardQrCode(String batchNo, Integer rewardLevel, BigDecimal rewardAmount) {
        this.batchNo = batchNo;
        this.rewardLevel = rewardLevel;
        this.rewardAmount = rewardAmount;
    }
}
