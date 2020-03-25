package com.converage.mapper.currency;

import com.converage.architecture.dto.Pagination;
import com.converage.entity.currency.huobi.HuobiCurrencyInfo;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface HuobiCurrencyMapper {
    Integer selectCountByCoinId(@Param("symbol") String symbol);

    List<HuobiCurrencyInfo> listCurrencyInfoBySymbol(@Param("symbol") String symbol, @Param("pagination") Pagination pagination);

    List<HuobiCurrencyInfo> listCurrencyInfoGroupBySymbol();
}
