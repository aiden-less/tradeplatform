package com.converage.service.transaction;

import com.alibaba.fastjson.JSONObject;
import com.converage.architecture.dto.Pagination;
import com.converage.architecture.exception.BusinessException;
import com.converage.architecture.service.BaseService;
import com.converage.client.netty.TradeMatchClient;
import com.converage.constance.*;
import com.converage.entity.assets.*;
import com.converage.entity.market.TradePair;
import com.converage.entity.transaction.CctOrder;
import com.converage.entity.user.User;
import com.converage.mapper.user.CctAssetsMapper;
import com.converage.mapper.user.CctFrozenAssetsMapper;
import com.converage.service.common.GlobalConfigService;
import com.converage.utils.ValueCheckUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.map.HashedMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallbackWithoutResult;
import org.springframework.transaction.support.TransactionTemplate;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.*;
import java.util.List;
import java.util.concurrent.ConcurrentSkipListMap;

import static com.converage.constance.ShopConst.TRANSACTION_BUY;

@Service
@Slf4j
public class CctService extends BaseService {
    private static final Logger logger = LoggerFactory.getLogger(CctService.class);

    //存储委托单Map
    public static Map<String, ConcurrentSkipListMap<String, CctOrder>> orderMap = new HashedMap(30);

    private static final String ORDER_BUY = "BUY";
    private static final String ORDER_SELL = "SELL";

    @Autowired
    private GlobalConfigService globalConfigService;

    @Autowired
    private CctAssetsMapper cctAssetsMapper;

    @Autowired
    private CctFrozenAssetsMapper cctFrozenAssetsMapper;

    @Autowired
    private TransactionTemplate transactionTemplate;

    @Autowired
    private TradeMatchClient tradeMatchClient;


    /**
     * 创建币币交易订单
     *
     * @param userId            用户id
     * @param transactionType   交易类型
     * @param transactionUnit   交易单价
     * @param transactionNumber 交易数量
     */
    public void createTransactionOrder(String userId, Integer transactionType, BigDecimal transactionUnit, BigDecimal transactionNumber, String tradePairId) {
        TradePair tradePair = selectOneById(tradePairId, TradePair.class);
        ValueCheckUtils.notEmpty(tradePair, "请选择交易对");

        User user = selectOneById(userId, User.class);
        ValueCheckUtils.notEmpty(user, "未找到用户信息");
        if (!user.getIfCanOtc()) {
            throw new BusinessException("您的交易权限已被禁止");
        }

        if (BigDecimal.ZERO.compareTo(transactionUnit) >= 0) {
            throw new BusinessException("交易单价必须大于0");
        }

        if (BigDecimal.ZERO.compareTo(transactionNumber) >= 0) {
            throw new BusinessException("交易数量必须大于0");
        }

        //订单信息
        CctOrder ato = new CctOrder();
        BigDecimal transactionAmount = transactionUnit.multiply(transactionNumber);
        ato.setUserId(userId);
        ato.setTransactionType(transactionType);
        ato.setTransactionTypeStr(transactionType == 1 ? ORDER_BUY : ORDER_SELL);
        ato.setTransactionUnit(transactionUnit);
        ato.setTransactionNumber(transactionNumber);
        ato.setTransactionSurplusNumber(transactionNumber);
        ato.setTransactionAmount(transactionAmount);
        ato.setStatus(CommonConst.UN_FINISH);
        ato.setCreateTime(new Timestamp(System.currentTimeMillis()));
        ato.setTradePairName(tradePair.getTradeCoinName());


        if (transactionType == TRANSACTION_BUY) {
            BigDecimal buyNumberMinLimit = new BigDecimal(globalConfigService.get(GlobalConfigService.Enum.BUY_NUMBER_MIN_LIMIT));
            BigDecimal buyNumberMaxLimit = new BigDecimal(globalConfigService.get(GlobalConfigService.Enum.BUY_NUMBER_MAX_LIMIT));
            BigDecimal buyUnitMinLimit = new BigDecimal(globalConfigService.get(GlobalConfigService.Enum.BUY_UNIT_MIN_LIMIT));
            BigDecimal buyUnitMaxLimit = new BigDecimal(globalConfigService.get(GlobalConfigService.Enum.BUY_UNIT_MAX_LIMIT));

            if (buyNumberMinLimit.compareTo(transactionNumber) > 0 || buyNumberMaxLimit.compareTo(transactionNumber) < 0) {
                throw new BusinessException("买入数量范围" + buyNumberMinLimit + "-" + buyNumberMaxLimit);
            }

            if (buyUnitMinLimit.compareTo(transactionUnit) > 0 || buyUnitMaxLimit.compareTo(transactionUnit) < 0) {
                throw new BusinessException("买入单价范围" + buyUnitMinLimit + "-" + buyUnitMaxLimit);
            }

        } else if (transactionType == ShopConst.TRANSACTION_SELL) {
            BigDecimal sellNumberMinLimit = new BigDecimal(globalConfigService.get(GlobalConfigService.Enum.SELL_NUMBER_MIN_LIMIT));
            BigDecimal sellNumberMaxLimit = new BigDecimal(globalConfigService.get(GlobalConfigService.Enum.SELL_NUMBER_MAX_LIMIT));
            BigDecimal sellUnitMinLimit = new BigDecimal(globalConfigService.get(GlobalConfigService.Enum.SELL_UNIT_MIN_LIMIT));
            BigDecimal sellUnitMaxLimit = new BigDecimal(globalConfigService.get(GlobalConfigService.Enum.SELL_UNIT_MAX_LIMIT));

            if (sellNumberMinLimit.compareTo(transactionNumber) > 0 || sellNumberMaxLimit.compareTo(transactionNumber) < 0) {
                throw new BusinessException("卖出数量范围" + sellNumberMinLimit + "-" + sellNumberMaxLimit);
            }

            if (sellUnitMinLimit.compareTo(transactionUnit) > 0 || sellUnitMaxLimit.compareTo(transactionUnit) < 0) {
                throw new BusinessException("卖出单价范围" + sellUnitMinLimit + "-" + sellUnitMaxLimit);
            }

        } else {
            throw new BusinessException("交易类型参数异常");
        }

        transactionTemplate.execute(new TransactionCallbackWithoutResult() {
            @Override
            protected void doInTransactionWithoutResult(TransactionStatus transactionStatus) {
                ValueCheckUtils.notZero(insertIfNotNull(ato), "下单失败");

                String orderId = ato.getId();
                String frozenCoinId = transactionType == 1 ? tradePair.getValuationCoinId() : tradePair.getTradeCoinId();
                BigDecimal frozenCoinAmount = transactionType == 1 ? transactionUnit.multiply(transactionAmount) : transactionNumber;

                CctFrozenAssets cctUserFrozenAssets = new CctFrozenAssets();
                cctUserFrozenAssets.setUserId(userId);
                cctUserFrozenAssets.setCoinId(frozenCoinId);
                cctUserFrozenAssets.setOrderId(orderId);
                cctUserFrozenAssets.setAssetsAmount(frozenCoinAmount);
                ValueCheckUtils.notZero(insertIfNotNull(cctUserFrozenAssets), "下单失败");

                //冻结资产
                ValueCheckUtils.notZero(cctAssetsMapper.decrease(userId, frozenCoinAmount, frozenCoinId), "资产不足");
            }
        });


        //推送消息至撮合服务器
        try {
            tradeMatchClient.action(JSONObject.toJSONString(ato));
        } catch (InterruptedException e) {
            logger.error("推送消息至撮合服务器失败", e.getMessage());
        }
    }


    //高于成交价的订单退款定时任务
    public void refundSurplusFrozenAssets() {
        String errorMsg = "多余冻结资产退款失败";
        List<CctFrozenAssets> cctUserFrozenAssetsList = cctFrozenAssetsMapper.listRefund(TransactionEnum.FINISH.getType());

        transactionTemplate.execute(new TransactionCallbackWithoutResult() {
            @Override
            protected void doInTransactionWithoutResult(TransactionStatus transactionStatus) {
                for (CctFrozenAssets cctUserFrozenAssets : cctUserFrozenAssetsList) {
                    BigDecimal increaseAmount = cctUserFrozenAssets.getAssetsAmount();
                    String userId = cctUserFrozenAssets.getUserId();
                    String coinId = cctUserFrozenAssets.getCoinId();

                    CctFrozenAssets cctUserFrozenAssetsUpdate = new CctFrozenAssets();
                    cctUserFrozenAssetsUpdate.setId(cctUserFrozenAssets.getId());
                    cctUserFrozenAssetsUpdate.setIfRefund(true);

                    ValueCheckUtils.notZero(updateIfNotNull(cctUserFrozenAssetsUpdate), errorMsg);
                    ValueCheckUtils.notZero(cctAssetsMapper.increase(userId, increaseAmount, coinId), errorMsg);
                }
            }
        });
    }


}


