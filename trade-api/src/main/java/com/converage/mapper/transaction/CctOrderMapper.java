package com.converage.mapper.transaction;

import com.converage.architecture.dto.Pagination;
import com.converage.entity.transaction.CctOrder;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;

@Repository
public interface CctOrderMapper {
    List<CctOrder> listTradingOrder(@Param("tradePairId") String tradePairId, @Param("type") int type);
}
