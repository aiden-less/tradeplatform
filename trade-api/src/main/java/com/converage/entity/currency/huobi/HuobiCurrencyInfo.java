package com.converage.entity.currency.huobi;

import com.converage.architecture.mybatis.annotation.Column;
import com.converage.architecture.mybatis.annotation.Id;
import com.converage.architecture.mybatis.annotation.Table;
import lombok.Data;
import org.apache.ibatis.type.Alias;

import java.io.Serializable;
import java.math.BigDecimal;

@Data
@Alias("HuobiCurrencyInfo")
@Table(name = "huobi_currency_info")
public class HuobiCurrencyInfo implements Serializable{

    private static final long serialVersionUID = 8658452734833264939L;
    @Id
    @Column(name = Id)
    private String id;

    @Column(name = Symbol_pair)
    private String symbolPair;

    @Column(name = Symbol)
    private String symbol;

    @Column(name = Exchange_name)
    private String exchangeName;

    @Column(name = Image)
    private String image;

    @Column(name = Current_CNY_Price)
    private BigDecimal currentCNYPrice;

    @Column(name = Current_USD_Price)
    private BigDecimal currentUSDPrice;

    @Column(name = Price_change_percentage)
    private BigDecimal priceChangePercentage;

    @Column(name = Total_supply)
    private BigDecimal totalSupply;


    private BigDecimal marketCap;
    private String summary;
    private String publishTime;
    private String circulateVolume;
    private String consensusMechanism;
    private String projectType;
    private String crowdfundingPrice;
    private String fullName;
    private String projectTeam;
    private String projectValuation;
    private String whitePaper;
    private String financingHistory;
    private String technicalCharacteristics;
    private String blockQuery;
    private String projectConsultant;
    private String officialWebsite;
    private String projectPosition;
    private String application;
    private String projectProgress;
    private String publishVolume;
    private String ICOProgress;

    public static final String Id = "id";
    public static final String Symbol_pair = "symbol_pair";
    public static final String Symbol = "symbol";
    public static final String Exchange_name = "exchange_name";
    public static final String Image = "image";
    public static final String Current_CNY_Price = "current_CNY_Price";
    public static final String Current_USD_Price = "current_USD_Price";
    public static final String Price_change_percentage = "price_change_percentage";
    public static final String Market_cap = "market_cap";
    public static final String Total_supply = "total_supply";


    public HuobiCurrencyInfo(){}

    public HuobiCurrencyInfo(String symbolPair,String symbol, String exchangeName, BigDecimal currentUSDPrice, BigDecimal currentCNYPrice, BigDecimal priceChangePercentage) {
        this.symbolPair = symbolPair;
        this.symbol = symbol;
        this.exchangeName = exchangeName;
        this.currentUSDPrice = currentUSDPrice;
        this.currentCNYPrice = currentCNYPrice;
        this.priceChangePercentage = priceChangePercentage;
    }
}
