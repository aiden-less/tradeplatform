package com.converage.entity.shop;

import com.converage.architecture.dto.Pagination;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

@Data
public class RewardQrCodeCreateInfo implements Serializable{
    private static final long serialVersionUID = 5925281024176705056L;

    private String id;
    //批次编号
    private String batchNo;

    private Integer firstRewardNumber;
    private BigDecimal firstRewardAmount;

    private Integer secondRewardNumber;
    private BigDecimal secondRewardAmount;

    private Integer thirtyRewardNumber;
    private BigDecimal thirtyRewardAmount;

    private Integer forthRewardNumber;
    private BigDecimal forthRewardAmount;

    private Integer fifthRewardNumber;
    private BigDecimal fifthRewardAmount;

    private Integer randomRewardNumber;//随机奖励数量
    private BigDecimal randomRewardAmount;//随机奖励最大值

    private BigDecimal rewardAmount;


    private Pagination pagination;
}
