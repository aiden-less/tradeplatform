package com.converage.mapper.user;

import com.converage.dto.AssetsFinanceQuery;
import com.converage.entity.assets.CctAssets;
import com.converage.entity.assets.CctFinanceLog;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;

@Repository
public interface CctAssetsMapper {


    Integer increaseUserAssets(@Param("userId") String userId, @Param("amount") BigDecimal amount, @Param("settlementId") Integer settlementId);

    Integer decreaseUserAssets(@Param("userId") String userId, @Param("amount") BigDecimal amount, @Param("settlementId") Integer settlementId);

    Integer increase(@Param("userId") String userId, @Param("amount") BigDecimal amount, @Param("coinId") String coinId);

    Integer decrease(@Param("userId") String userId, @Param("amount") BigDecimal amount, @Param("coinId") String coinId);

    List<CctAssets> listUserAssets(@Param("userId") String userId);

    CctAssets getUserAssets( AssetsFinanceQuery AssetsFinanceQuery);

    List<CctFinanceLog> listFinanceLog(AssetsFinanceQuery AssetsFinanceQuery);
}
