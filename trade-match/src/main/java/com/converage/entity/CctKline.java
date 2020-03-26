package com.converage.entity;

import lombok.Data;
import org.springframework.data.mongodb.core.mapping.Document;

import java.math.BigDecimal;
import java.sql.Timestamp;

/**
 * Created by 旺旺 on 2020/3/19.
 */
@Data
@Document
public class CctKline {

    private String id;
    private BigDecimal fresh; //最新价
    private BigDecimal open; //开盘价
    private BigDecimal close; //收盘价
    private BigDecimal high; //最高价
    private BigDecimal low; //最低价
    private Timestamp createTime; //创建时间
    private String tradeCoinName; //交易币种名
    private BigDecimal tradeCoinVolume; //交易币种交易量
    private String valuationCoinName; //计价币种名
    private BigDecimal valuationCoinVolume; //计价币种交易量
}
