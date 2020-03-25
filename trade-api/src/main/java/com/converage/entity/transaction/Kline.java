package com.converage.entity.transaction;

import lombok.Data;
import org.apache.ibatis.type.Alias;

import java.math.BigDecimal;
import java.sql.Timestamp;

@Data
@Alias("Kline")
public class Kline {
    private Timestamp id;
    private BigDecimal open;
    private BigDecimal close;
    private BigDecimal high;
    private BigDecimal low;
    private BigDecimal amount;
}
