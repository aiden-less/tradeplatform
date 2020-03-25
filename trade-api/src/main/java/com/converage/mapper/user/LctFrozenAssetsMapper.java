package com.converage.mapper.user;

import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;

/**
 * Created by 旺旺 on 2020/3/16.
 */
@Repository
public interface LctFrozenAssetsMapper {

    Integer increase(@Param("userId") String userId, @Param("amount") BigDecimal amount, @Param("coinId") String coinId);

    Integer decrease(@Param("userId") String userId, @Param("amount") BigDecimal amount, @Param("coinId") String coinId);
}
