package com.converage.mapper.user;

import com.converage.dto.AssetsFinanceQuery;
import com.converage.entity.assets.CctFinanceLog;
import com.converage.entity.assets.LctAssets;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;

@Repository
public interface LctAssetsMapper {
    Integer increase(@Param("userId") String userId, @Param("amount") BigDecimal amount, @Param("coinId") String coinId);

    Integer decrease(@Param("userId") String userId, @Param("amount") BigDecimal amount, @Param("coinId") String coinId);

    List<LctAssets> listUserAssets(@Param("userId") String userId);

    LctAssets getUserAssets(AssetsFinanceQuery assetsFinanceQuery);

    List<CctFinanceLog> listFinanceLog(AssetsFinanceQuery assetsFinanceQuery);
}
