package com.converage.mapper.shop;

import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface RewardQrCodeMapper {

    Integer updateQrCodeDownloadStatus(@Param("batchNo") String batchNo);

    Integer cancelRewardQrcode(@Param("rewardCodeId") String rewardCodeId);
}
