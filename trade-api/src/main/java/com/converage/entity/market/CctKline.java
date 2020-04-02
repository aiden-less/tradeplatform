package com.converage.entity.market;

import com.converage.architecture.mybatis.annotation.Column;
import com.converage.architecture.mybatis.annotation.Id;
import com.converage.jdbc.annotation.Table;
import lombok.Data;
import org.springframework.data.mongodb.core.mapping.Document;

import java.io.Serializable;
import java.math.BigDecimal;
import java.sql.Timestamp;

/**
 * Created by 旺旺 on 2020/3/19.
 */
@Data
@Table(name = "cct_kline")
public class CctKline implements Serializable {

    private static final long serialVersionUID = 7235424802974726927L;

    @Id
    @Column(name = "id")
    private String id;

    @Column(name = "type")
    private String type;

    @Column(name = "fresh")
    private BigDecimal fresh; //最新价

    @Column(name = "open")
    private BigDecimal open; //开盘价

    @Column(name = "close")
    private BigDecimal close; //收盘价

    @Column(name = "high")
    private BigDecimal high; //最高价

    @Column(name = "low")
    private BigDecimal low; //最低价

    @Column(name = "createTime")
    private Timestamp createTime; //创建时间

    @Column(name = "createTime")
    private String tradeCoinName; //交易币种名

    @Column(name = "createTime")
    private BigDecimal tradeCoinVolume; //交易币种交易量

    @Column(name = "createTime")
    private String valuationCoinName; //计价币种名

    @Column(name = "createTime")
    private BigDecimal valuationCoinVolume; //计价币种交易量

}
