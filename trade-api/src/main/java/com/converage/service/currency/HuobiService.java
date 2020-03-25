package com.converage.service.currency;

import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.ImmutableMap;
import com.converage.architecture.service.BaseService;
import com.converage.client.RedisClient;
import com.converage.entity.currency.ExchangeRate;
import com.converage.entity.currency.coingecko.CoingeckoCurrencyInfo;
import com.converage.entity.currency.huobi.*;
import com.converage.mapper.currency.HuobiCurrencyMapper;
import com.converage.utils.BigDecimalUtils;
import com.converage.utils.EnvironmentUtils;
import com.converage.utils.HttpClientUtils;
import com.converage.utils.ValueCheckUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.converage.constance.RedisKeyConst.*;

@Service
public class HuobiService extends BaseService {

    String regxpUSDT = "usdt";
    String regxpHUSD = "husd";
    String regxpBTC = "btc";
    String regxpETH = "eth";
    String regxpHT = "ht";

    @Autowired
    private RedisClient redisClient;

    @Autowired
    private HuobiCurrencyMapper huobiCurrencyMapper;

    @Autowired
    private EnvironmentUtils environmentUtils;

    public void getCurrencyInfo() throws IOException {
        if (!environmentUtils.isPro()) {
            return;
        }
        //获取人民币汇率
        String cnyToUsdRateUrl = "https://cdn.jsdelivr.net/gh/prebid/currency-file@1/latest.json";
        String result1 = HttpClientUtils.doGet(cnyToUsdRateUrl, new HashMap<>(), HttpClientUtils.CHARSET);
        ExchangeRate exchangeRate = JSONObject.parseObject(result1, ExchangeRate.class);
        BigDecimal CNYToUsdRate = exchangeRate.getConversions().get("USD").get("CNY");


        String url = "https://api.huobipro.com/market/tickers";
        Map<String, String> paramMap = new HashMap<>();
        String result = HttpClientUtils.doGet(url, paramMap, HttpClientUtils.CHARSET);

        ValueCheckUtils.notEmpty(result, "");
        HuobiMarketRsp huobiMarketRsp = JSONObject.parseObject(result, HuobiMarketRsp.class);
        if (!"ok".equals(huobiMarketRsp.getStatus())) {
            return;
        }

        List<HuobiMarket> marketList = huobiMarketRsp.getData();
        List<String> coinGeckoFieldList = Arrays.asList(CoingeckoCurrencyInfo.Image, CoingeckoCurrencyInfo.Total_Supply);
        List<String> huobiFieldList = Arrays.asList(HuobiCurrencyInfo.Id);

        marketList.parallelStream().forEach(huobiMarket -> {
            BigDecimal close = huobiMarket.getClose();
            BigDecimal open = huobiMarket.getOpen();
            BigDecimal priceChangePercentage = BigDecimalUtils.divide(close.subtract(open), close).multiply(BigDecimal.valueOf(100));

            String symbolPair = huobiMarket.getSymbol();
            String symbol = matcherSymbol(symbolPair);

            if (StringUtils.isNotEmpty(symbol)) {
                BigDecimal currentUSDPrice = huobiMarket.getClose().setScale(BigDecimalUtils.SCALE_EIGHT, BigDecimal.ROUND_DOWN);
                BigDecimal currentCNYPrice = currentUSDPrice.multiply(CNYToUsdRate).setScale(BigDecimalUtils.SCALE_EIGHT, BigDecimal.ROUND_DOWN);

                HuobiCurrencyInfo huobiCurrencyInfo = new HuobiCurrencyInfo(symbolPair, symbol, HUOBI, currentUSDPrice, currentCNYPrice, priceChangePercentage);
                HuobiCurrencyInfo huobiCurrencyInfoPo = selectiveOneByWhereString(huobiFieldList, HuobiCurrencyInfo.Symbol_pair + "=", symbolPair, HuobiCurrencyInfo.class);

                if (huobiCurrencyInfoPo == null) {
                    CoingeckoCurrencyInfo coingeckoCurrencyInfoPo = selectiveOneByWhereString(
                            coinGeckoFieldList, CoingeckoCurrencyInfo.Symbol + "=", symbol, CoingeckoCurrencyInfo.class
                    );
                    if (coingeckoCurrencyInfoPo != null) {
                        huobiCurrencyInfo.setImage(coingeckoCurrencyInfoPo.getImage());
                        BigDecimal totalSupply = coingeckoCurrencyInfoPo.getTotalSupply() == null ? coingeckoCurrencyInfoPo.getCirculatingSupply() : coingeckoCurrencyInfoPo.getTotalSupply();
                        huobiCurrencyInfo.setTotalSupply(totalSupply);
                        insert(huobiCurrencyInfo);
                    }
                } else {
                    BeanUtils.copyProperties(huobiCurrencyInfo, huobiCurrencyInfoPo, new String[]{HuobiCurrencyInfo.Id, HuobiCurrencyInfo.Image});
                    updateIfNotNull(huobiCurrencyInfoPo);
                }
            }

        });

    }

    public String matcherSymbol(String symbol) {
        if (symbol.indexOf(regxpUSDT) > 0) {
            return symbol.substring(0, symbol.indexOf(regxpUSDT));
        }
        if (symbol.indexOf(regxpHUSD) > 0) {
            return symbol.substring(0, symbol.indexOf(regxpHUSD));
        }
        if (symbol.indexOf(regxpBTC) > 0) {
            return symbol.substring(0, symbol.indexOf(regxpBTC));
        }
        if (symbol.indexOf(regxpETH) > 0) {
            return symbol.substring(0, symbol.indexOf(regxpETH));
        }
        if (symbol.indexOf(regxpHT) > 0) {
            return symbol.substring(0, symbol.indexOf(regxpHT));
        }
        return "";
    }


    public void updateCurrencyInfo() {
        List<HuobiCurrencyInfo> currencyInfoList = huobiCurrencyMapper.listCurrencyInfoGroupBySymbol();
        Pattern r = Pattern.compile("[1-9]\\d*\\.?\\d*");
        currencyInfoList.parallelStream().forEach(huobiCurrencyInfoPo -> {
            String symbol = huobiCurrencyInfoPo.getSymbol();
            Map<String, String> whereMap = ImmutableMap.of(
                    "r", "h7ccnrrma2p",
                    "lang", "zh-cn",
                    "currency", symbol
            );
            String url = "https://www.huobi.co/-/x/hb/p/api/contents/pro/currency_introduction";
            String result = HttpClientUtils.doGet(url, whereMap, HttpClientUtils.CHARSET);
            HuobiCurrencyRsp entity = JSONObject.parseObject(result, HuobiCurrencyRsp.class);
            HuobiCurrencyInfo huobiCurrencyInfoVo = entity.getData();

            BigDecimal totalSupply = BigDecimal.ZERO;
            String circulateVolume = huobiCurrencyInfoVo.getCirculateVolume();
            if (StringUtils.isNotEmpty(circulateVolume)) {
                circulateVolume = circulateVolume.replace(",", "");
                Matcher m = r.matcher(circulateVolume);
                if (m.find()) {
                    BigDecimal bigDecimal = new BigDecimal(m.group());
                    if (circulateVolume.contains("万")) {
                        totalSupply = bigDecimal.multiply(BigDecimal.valueOf(10000));
                    } else if (circulateVolume.contains("亿")) {
                        totalSupply = bigDecimal.multiply(BigDecimal.valueOf(100000000));
                    } else if (circulateVolume.contains("%")) {
                        String publishVolumeStr = huobiCurrencyInfoVo.getPublishVolume().replace(",", "");
                        Matcher m1 = r.matcher(publishVolumeStr);
                        if (m1.find()) {
                            BigDecimal publishVolume = new BigDecimal(m1.group());
                            totalSupply = BigDecimalUtils.divide(publishVolume.multiply(bigDecimal), BigDecimal.valueOf(100));
                        }
                    } else {
                        totalSupply = bigDecimal;
                    }
                }
            }


            HuobiCurrencyInfo huobiCurrencyInfoUpdate = new HuobiCurrencyInfo();
            huobiCurrencyInfoUpdate.setId(huobiCurrencyInfoPo.getId());
            huobiCurrencyInfoUpdate.setTotalSupply(totalSupply);
            updateIfNotNull(huobiCurrencyInfoUpdate);
        });
    }

}
