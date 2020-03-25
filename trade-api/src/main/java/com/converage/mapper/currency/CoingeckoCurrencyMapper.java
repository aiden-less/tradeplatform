package com.converage.mapper.currency;

import com.converage.entity.currency.coingecko.CoingeckoCurrencyInfo;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CoingeckoCurrencyMapper {
    Integer selectCountByCoinId(@Param("coinId") String coinId);

    List<CoingeckoCurrencyInfo> listUserCollect(@Param("coinIds") List<String> coinIds);
}
