package com.converage.mapper.transaction;

import org.springframework.stereotype.Repository;

import java.math.BigDecimal;

@Repository
public interface LctMerchantOrderMapper {
    int decreaseSurplusNumber(String orderId, BigDecimal number);
}
