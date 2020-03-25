package com.converage.mapper.user;

import com.converage.entity.assets.CctFrozenAssets;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;

/**
 * Created by 旺旺 on 2020/3/16.
 */
@Repository
public interface CctFrozenAssetsMapper {

    Integer increase(@Param("userId") String userId, @Param("amount") BigDecimal amount, @Param("coinId") String coinId);

    Integer decrease(@Param("userId") String userId, @Param("amount") BigDecimal amount, @Param("coinId") String coinId);

    List<CctFrozenAssets> listRefund(@Param("status") int status, @Param("limit") int limit);
}
