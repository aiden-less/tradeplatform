package com.converage.entity.currency.coingecko;

import com.converage.architecture.mybatis.annotation.Column;
import com.converage.architecture.mybatis.annotation.Id;
import com.converage.architecture.mybatis.annotation.Table;
import com.converage.utils.BigDecimalUtils;
import lombok.Data;
import org.apache.ibatis.type.Alias;

import java.io.Serializable;
import java.math.BigDecimal;

@Data
@Alias("CoingeckoCurrencyInfo")
@Table(name = "coingecko_currency_info")
public class CoingeckoCurrencyInfo implements Serializable {
    private static final long serialVersionUID = -7883885415605323125L;

    @Id
    @Column(name = Id)
    private String id;

    @Column(name = Coin_id)
    private String coinId;

    @Column(name = Symbol)
    private String symbol;

    @Column(name = Name)
    private String name;

    @Column(name = Image)
    private String image;

    @Column(name = Current_CNY_price)
    private BigDecimal currentCNYPrice;

    @Column(name = Current_USD_price)
    private BigDecimal currentUSDPrice;

    @Column(name = Market_cap)
    private BigDecimal marketCap;

    @Column(name = Market_cap_rank)
    private Integer marketCapRank;

    @Column(name = Total_volume)
    private BigDecimal totalVolume;

    @Column(name = High_price)
    private BigDecimal highPrice;

    @Column(name = Low_price)
    private BigDecimal lowPrice;

    @Column(name = Price_change)
    private BigDecimal priceChange;

    @Column(name = Price_change_percentage)
    private BigDecimal priceChangePercentage;

    @Column(name = Market_cap_change)
    private BigDecimal marketCapChange;

    @Column(name = Market_cap_change_percentage)
    private BigDecimal marketCapChangePercentage;

    @Column(name = Circulating_supply)
    private BigDecimal circulatingSupply;

    @Column(name = Total_Supply)
    private BigDecimal totalSupply;

    @Column(name = Ath)
    private BigDecimal ath;

    @Column(name = Ath_Change_percentage)
    private BigDecimal athChangePercentage;

    @Column(name = Ath_date)
    private String athDate;

    private String exchangeName = "Coingecko";

    //从火币接口获取的信息
    private String introduction;
    private String publishTime;
    private String fullName;
    private String whitePaper;
    private String blockQuery;
    private String officialWebsite;
    private Boolean ifCollect;


    //DB Column name
    public static final String Id = "id";
    public static final String Coin_id = "coin_id";
    public static final String Symbol = "symbol";
    public static final String Name = "name";
    public static final String Image = "image";
    public static final String Current_CNY_price = "current_CNY_price";
    public static final String Current_USD_price = "current_USD_price";
    public static final String Market_cap = "market_cap";
    public static final String Market_cap_rank = "market_cap_rank";
    public static final String Total_volume = "total_volume";
    public static final String High_price = "high_price";
    public static final String Low_price = "low_price";
    public static final String Price_change = "price_change";
    public static final String Price_change_percentage = "price_change_percentage";
    public static final String Market_cap_change = "market_cap_change";
    public static final String Market_cap_change_percentage = "market_cap_change_percentage";
    public static final String Circulating_supply = "circulating_supply";
    public static final String Total_Supply = "total_Supply";
    public static final String Ath = "ath";
    public static final String Ath_Change_percentage = "ath_Change_percentage";
    public static final String Ath_date = "ath_date";


    public CoingeckoCurrencyInfo() {
    }

    public CoingeckoCurrencyInfo(CoingeckoCurrencyApiEntity currencyApiInfo) {
        this.coinId = currencyApiInfo.getId();
        this.symbol = currencyApiInfo.getSymbol();
        this.name = currencyApiInfo.getName();
        this.image = currencyApiInfo.getImage();
        this.currentCNYPrice = currencyApiInfo.getCurrent_price();
        this.marketCap = currencyApiInfo.getMarket_cap();
        this.marketCapRank = currencyApiInfo.getMarket_cap_rank();
        this.totalVolume = currencyApiInfo.getTotal_volume();
        this.highPrice = currencyApiInfo.getHigh_24h();
        this.lowPrice = currencyApiInfo.getLow_24h();
        this.priceChange = currencyApiInfo.getPrice_change_24h();
        this.priceChangePercentage = currencyApiInfo.getPrice_change_percentage_24h();
        this.marketCapChange = currencyApiInfo.getMarket_cap_change_24h();
        this.marketCapChangePercentage = currencyApiInfo.getMarket_cap_change_percentage_24h();
        this.circulatingSupply = currencyApiInfo.getCirculating_supply();
        this.totalSupply = currencyApiInfo.getTotal_supply();
        this.ath = currencyApiInfo.getAth();
        this.athChangePercentage = currencyApiInfo.getAth_change_percentage();
        this.athDate = currencyApiInfo.getAth_date();
    }

    public void setUSDPrice(BigDecimal exchangeRate) {
        if (this.currentCNYPrice == null) {
            return;
        }
        this.currentUSDPrice = BigDecimalUtils.divide(this.currentCNYPrice, exchangeRate);
    }
}
