package com.converage.entity.transaction;

import com.converage.architecture.mybatis.annotation.Column;
import com.converage.architecture.mybatis.annotation.Id;
import com.converage.architecture.mybatis.annotation.Table;
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

    private static final long serialVersionUID = 1400731379754699292L;

    @Id
    @Column(name = Id)
    private String id;

    @Column(name = Type)
    private String type;

    @Column(name = Fresh)
    private BigDecimal fresh; //最新价

    @Column(name = Open)
    private BigDecimal open; //开盘价

    @Column(name = Close)
    private BigDecimal close; //收盘价

    @Column(name = High)
    private BigDecimal high; //最高价

    @Column(name = Low)
    private BigDecimal low; //最低价

    @Column(name = Create_time)
    private Timestamp createTime; //创建时间

    @Column(name = Trade_coin_name)
    private String tradeCoinName; //交易币种名

    @Column(name = Trade_Coin_Volume)
    private BigDecimal tradeCoinVolume; //交易币种交易量

    @Column(name = Valuation_coin_name)
    private String valuationCoinName; //计价币种名

    @Column(name = Valuation_Coin_volume)
    private BigDecimal valuationCoinVolume; //计价币种交易量


    //DB Column name
    public static final String Id = "id";
    public static final String Type = "type";
    public static final String Fresh = "fresh";
    public static final String Open = "open";
    public static final String Close = "close";
    public static final String High = "high";
    public static final String Low = "low";
    public static final String Create_time = "create_time";
    public static final String Trade_coin_name = "trade_coin_name";
    public static final String Trade_Coin_Volume = "trade_Coin_Volume";
    public static final String Valuation_coin_name = "valuation_coin_name";
    public static final String Valuation_Coin_volume = "valuation_Coin_volume";
}
