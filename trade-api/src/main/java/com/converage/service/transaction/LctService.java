package com.converage.service.transaction;

import com.converage.architecture.utils.UUIDUtils;
import com.converage.constance.LctOrderStatus;
import com.converage.mapper.transaction.LctOrderLogMapper;
import com.google.common.collect.ImmutableMap;
import com.converage.architecture.exception.BusinessException;
import com.converage.architecture.service.BaseService;
import com.converage.constance.TransactionEnum;
import com.converage.entity.assets.CctAssets;
import com.converage.entity.transaction.LctOrderLog;
import com.converage.entity.transaction.LctMerchantOrder;
import com.converage.mapper.transaction.LctMerchantOrderMapper;
import com.converage.mapper.user.LctAssetsMapper;
import com.converage.mapper.user.LctFrozenAssetsMapper;
import com.converage.utils.ValueCheckUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallbackWithoutResult;
import org.springframework.transaction.support.TransactionTemplate;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.Map;
import java.util.Objects;

/**
 * Created by 旺旺 on 2020/3/20.
 */
@Service
public class LctService extends BaseService {

    @Autowired
    private TransactionTemplate transactionTemplate;

    @Autowired
    private LctAssetsMapper lctAssetsMapper;

    @Autowired
    private LctOrderLogMapper lctOrderLogMapper;

    @Autowired
    private LctFrozenAssetsMapper lctFrozenAssetsMapper;

    @Autowired
    private LctMerchantOrderMapper lctMerchantOrderMapper;

    //创建法币广告
    public void createOrder(String userId, String coinId, Integer transactionType, BigDecimal transactionUnit, BigDecimal transactionNumber) {
        Map<String, Object> whereMap = ImmutableMap.of(
                CctAssets.User_id + "=", userId,
                CctAssets.Coin_id + "=", coinId
        );

        CctAssets cctAssets = selectOneByWhereMap(whereMap, CctAssets.class);

        if (transactionUnit.compareTo(BigDecimal.ZERO) <= 0) {
            throw new BusinessException("价格必须大于0");
        }

        if (transactionNumber.compareTo(BigDecimal.ZERO) <= 0) {
            throw new BusinessException("数量必须大于0");
        }

        if (Objects.equals(transactionType, TransactionEnum.SELL.getType()) && transactionNumber.compareTo(cctAssets.getAssetsAmount()) > 0) {
            throw new BusinessException("法币资产不足");
        }


        LctMerchantOrder lctMerchantOrder = new LctMerchantOrder();
        lctMerchantOrder.setUserId(userId);
        lctMerchantOrder.setCoinId(coinId);
        lctMerchantOrder.setTransactionType(transactionType);
        lctMerchantOrder.setTransactionUnit(transactionUnit);
        lctMerchantOrder.setTransactionNumber(transactionNumber);
        lctMerchantOrder.setTransactionSurplusNumber(transactionNumber);
        lctMerchantOrder.setTransactionAmount(transactionUnit.multiply(transactionNumber));
        lctMerchantOrder.setCreateTime(new Timestamp(System.currentTimeMillis()));
        lctMerchantOrder.setStatus(TransactionEnum.UN_FINISH.getType());

        transactionTemplate.execute(new TransactionCallbackWithoutResult() {
            @Override
            protected void doInTransactionWithoutResult(TransactionStatus transactionStatus) {
                ValueCheckUtils.notZero(insertIfNotNull(lctMerchantOrder), "创建订单失败");
                if (Objects.equals(transactionType, TransactionEnum.SELL.getType())) {
                    ValueCheckUtils.notZero(lctAssetsMapper.decrease(userId, transactionNumber, coinId), "资产不足");
                    ValueCheckUtils.notZero(lctFrozenAssetsMapper.increase(userId, transactionNumber, coinId), "资产不足");
                }
            }
        });
    }

    //创建法币用户订单 （用户操作）
    public void matchOrder(String userId, String merchantOrderId, BigDecimal transactionNumber) {
        LctMerchantOrder lctMerchantOrder = selectOneById(merchantOrderId, LctMerchantOrder.class);
        BigDecimal orderSurplusNumber = lctMerchantOrder.getTransactionSurplusNumber();

        if (transactionNumber.compareTo(orderSurplusNumber) > 0) {
            throw new BusinessException("订单余额不足");
        }

        String merchantUserId = lctMerchantOrder.getUserId();
        Integer merchantTransactionType = lctMerchantOrder.getTransactionType();

        Integer userTransactionType = Objects.equals(merchantTransactionType, TransactionEnum.BUY.getType())
                ? TransactionEnum.SELL.getType()
                : TransactionEnum.BUY.getType();

        String buyerUserId = Objects.equals(merchantTransactionType, TransactionEnum.BUY.getType()) ? merchantUserId : userId;
        String sellerUserId = Objects.equals(merchantTransactionType, TransactionEnum.BUY.getType()) ? userId : merchantUserId;

        String orderNo = String.valueOf((int) (100000 + Math.random() * 9900000)) + System.currentTimeMillis();

        String uId = UUIDUtils.createUUID();

        //散户订单交易记录
        LctOrderLog investorOrderLog = new LctOrderLog();
        investorOrderLog.setUid(uId);
        investorOrderLog.setUserId(userId);
        investorOrderLog.setOrderNo(orderNo);
        investorOrderLog.setBuyerUserId(buyerUserId);
        investorOrderLog.setSellerUserId(sellerUserId);
        investorOrderLog.setCoinId(lctMerchantOrder.getCoinId());
        investorOrderLog.setTransactionType(userTransactionType);
        investorOrderLog.setLctMerchantOrderId(merchantOrderId);
        investorOrderLog.setDoneUnit(lctMerchantOrder.getTransactionUnit());
        investorOrderLog.setDoneNumber(transactionNumber);
        investorOrderLog.setCreateTime(new Timestamp(System.currentTimeMillis()));
        investorOrderLog.setStatus(LctOrderStatus.UN_FINISH.getStatus());


        //商户订单交易记录
        LctOrderLog merchantOrderLog = new LctOrderLog();
        merchantOrderLog.setUid(uId);
        merchantOrderLog.setUserId(lctMerchantOrder.getUserId());
        merchantOrderLog.setOrderNo(orderNo);
        merchantOrderLog.setBuyerUserId(userId);
        merchantOrderLog.setSellerUserId(lctMerchantOrder.getUserId());
        merchantOrderLog.setCoinId(lctMerchantOrder.getCoinId());
        merchantOrderLog.setTransactionType(merchantTransactionType);
        merchantOrderLog.setLctMerchantOrderId(merchantOrderId);
        merchantOrderLog.setDoneUnit(lctMerchantOrder.getTransactionUnit());
        merchantOrderLog.setDoneNumber(transactionNumber);
        merchantOrderLog.setCreateTime(new Timestamp(System.currentTimeMillis()));
        merchantOrderLog.setStatus(LctOrderStatus.UN_FINISH.getStatus());

        transactionTemplate.execute(new TransactionCallbackWithoutResult() {
            @Override
            protected void doInTransactionWithoutResult(TransactionStatus transactionStatus) {
                ValueCheckUtils.notZero(insertIfNotNull(investorOrderLog), "散户订单交易记录创建失败");
                ValueCheckUtils.notZero(insertIfNotNull(merchantOrderLog), "商户订单交易记录创建失败");
                ValueCheckUtils.notZero(lctMerchantOrderMapper.decreaseSurplusNumber(merchantOrderId, transactionNumber), "订单余额不足");
            }
        });
    }


    /**
     * 我已付款/我已收款
     *
     * @param userId
     * @param lctOrderLogId
     */
    public void finishOrder(String userId, String lctOrderLogId) {
        ValueCheckUtils.notEmptyString(lctOrderLogId, "请选择订单");

        Map<String, Object> whereMap = ImmutableMap.of(
                LctOrderLog.Id + "=", lctOrderLogId,
                LctOrderLog.User_id + "=", userId
        );
        LctOrderLog lctOrderLog = selectOneByWhereMap(whereMap, LctOrderLog.class);


        int unFinishStatus = LctOrderStatus.UN_FINISH.getStatus();
        int finishStatus = LctOrderStatus.FINISH.getStatus();

        if (lctOrderLog.getStatus() != unFinishStatus) {
            throw new BusinessException("订单已经处理");
        }


        ValueCheckUtils.notEmpty(lctOrderLog, "未找到订单记录");

        int transactionType = lctOrderLog.getTransactionType();
        String uid = lctOrderLog.getUid();

        if (transactionType == TransactionEnum.BUY.getType()) {//我已付款
            ValueCheckUtils.notZero(lctOrderLogMapper.updateIfPayFlag(uid, true, unFinishStatus), "订单已经处理");

            //TODO 短信通知卖家放行


        } else {//我已收款
            if (lctOrderLog.getIfPay()) {
                throw new BusinessException("买家尚未确认付款");
            }


            String buyerUserId = lctOrderLog.getBuyerUserId();
            String sellerUserId = lctOrderLog.getSellerUserId();
            BigDecimal transferNumber = lctOrderLog.getDoneNumber();
            String coinId = lctOrderLog.getCoinId();

            transactionTemplate.execute(new TransactionCallbackWithoutResult() {
                @Override
                protected void doInTransactionWithoutResult(TransactionStatus transactionStatus) {
                    ValueCheckUtils.notZero(lctOrderLogMapper.updateStatus(uid, finishStatus, unFinishStatus), "订单已经处理");

                    //增加买家法币资产
                    ValueCheckUtils.notZero(lctAssetsMapper.increase(buyerUserId, transferNumber, coinId), "订单处理失败");
                    //减少卖家法币冻结资产
                    ValueCheckUtils.notZero(lctFrozenAssetsMapper.decrease(sellerUserId, transferNumber, coinId), "订单处理失败");
                }
            });

        }
    }

}
