package com.converage.entity.market;

import com.converage.architecture.mybatis.annotation.Column;
import com.converage.architecture.mybatis.annotation.Id;
import com.converage.architecture.mybatis.annotation.Table;
import lombok.Data;
import org.apache.ibatis.type.Alias;

import java.io.Serializable;

/**
 * Created by 旺旺 on 2020/3/14.
 */
@Data
@Alias("TradePair")
@Table(name = "trade_pair")
public class TradePair implements Serializable{

    private static final long serialVersionUID = -9074395258914660957L;
    @Id
    @Column(name = Id)
    private String id;

    @Column(name = Trade_coin_id)
    private String tradeCoinId; //交易币种Id

    @Column(name = Trade_coin_name)
    private String tradeCoinName; //交易币种名

    @Column(name = Valuation_coin_id)
    private String valuationCoinId; //计价币种Id

    @Column(name = Valuation_coin_name)
    private String valuationCoinName; //计价币种名

    @Column(name = Pair_name)
    private String pairName; //交易对名

    @Column(name = If_Valid)
    private Boolean ifValid; //

    @Column(name = If_Advertise)
    private Boolean ifAdvertise; //

    @Column(name = Advertise_sort)
    private int advertiseSort; //

    //DB Column name
    public static final String Id = "id";
    public static final String Trade_coin_id = "trade_coin_id";
    public static final String Trade_coin_name = "trade_coin_name";
    public static final String Valuation_coin_id = "valuation_coin_id";
    public static final String Valuation_coin_name = "valuation_coin_name";
    public static final String Pair_name = "pair_name";
    public static final String If_Valid = "if_Valid";
    public static final String If_Advertise = "if_Advertise";
    public static final String Advertise_sort = "advertise_sort";
}
