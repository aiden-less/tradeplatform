package com.converage.service;


import com.converage.constance.RedisKeyEnum;
import com.converage.constance.TransactionEnum;
import com.converage.entity.CctOrderMatchLog;
import com.converage.entity.TradePair;
import com.converage.init.CctOrderInit;
import com.converage.middleware.redis.RedisClient;
import com.converage.utils.ValueCheckUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallbackWithoutResult;
import org.springframework.transaction.support.TransactionTemplate;
import com.converage.entity.CctOrder;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Queue;

/**
 * Created by 旺旺 on 2020/3/18.
 */
@Service
public class TransactionService {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private RedisClient redisClient;

    @Autowired
    private UserCoinAssetsService userCoinAssetsService;

    @Autowired
    private CctKlineService cctKlineService;

    @Autowired
    private UserFrozenCoinAssetsService userFrozenCoinAssetsService;

    @Autowired
    private TransactionOrderDetailService transactionOrderDetailService;

    @Autowired
    private TransactionTemplate transactionTemplate;

    //撮合成交
    public void matherOrder(CctOrder newAto) {
        String tradePairName = newAto.getTradePairName();
        TradePair tradePair = (TradePair) redisClient.getHashKey(RedisKeyEnum.TradePair.name(), newAto.getTradePairId());

        Integer buyType = TransactionEnum.BUY.getType();
        Integer sellType = TransactionEnum.SELL.getType();

        Integer transactionType = newAto.getTransactionType();
        Integer reverseTransactionType = Objects.equals(transactionType, buyType) ? sellType : buyType;


        String mapKey = CctOrderInit.buildCctOrderQueueKey(tradePairName, transactionType);
        String reverseMapKey = CctOrderInit.buildCctOrderQueueKey(tradePairName, reverseTransactionType);

        //买的对应 -》 卖 ， 卖的对应 ->》买

        //归属委托队列
        Queue<CctOrder> atoQueue = CctOrderInit.cctOrderQueueMap.get(mapKey);

        //对应委托队列
        Queue<CctOrder> reverseAtoQueue = CctOrderInit.cctOrderQueueMap.get(reverseMapKey);
        CctOrder waitAto = reverseAtoQueue.poll();

        CctOrder buyOrder;
        CctOrder sellOrder;

        //新增委托单无论是买单还是卖单，买单价格大于或等于卖单价格时，才撮合成交，否则订单回到归属队列
        if (transactionType == buyType) {
            if (waitAto == null || newAto.getTransactionUnit().compareTo(waitAto.getTransactionUnit()) < 0) { //买单小于卖单价格
                atoQueue.add(newAto);
                return;
            }
            buyOrder = newAto;
            sellOrder = waitAto;
        } else { //卖单
            if (waitAto == null || newAto.getTransactionUnit().compareTo(waitAto.getTransactionUnit()) > 0) { //卖单价格大于买单价格
                atoQueue.add(newAto);
                return;
            }
            buyOrder = waitAto;
            sellOrder = newAto;
        }

        /**
         * 1.判断对应的委托单是否有可撮合的订单数据
         * 2.按撮合的两个订单之间最低价和最低数量进行撮合，未成交部分回到原队列
         */
        //成交价
        BigDecimal doneUnit = newAto.getTransactionUnit().min(waitAto.getTransactionUnit());
        //成交数
        BigDecimal doneNumber = newAto.getTransactionSurplusNumber().min(waitAto.getTransactionSurplusNumber());

        //成交订单idList
        List<String> finishOrderIdList = new ArrayList<>();

        //订单剩余数量处理 start
        BigDecimal newAtoNumber = newAto.getTransactionSurplusNumber();
        newAto.setTransactionSurplusNumber(newAtoNumber.subtract(doneNumber));

        if (newAto.getTransactionSurplusNumber().compareTo(BigDecimal.ZERO) > 0) {
            atoQueue.add(newAto);
        } else {
            finishOrderIdList.add(newAto.getId());
        }

        BigDecimal waitAtoNumber = waitAto.getTransactionSurplusNumber();
        waitAto.setTransactionSurplusNumber(waitAtoNumber.subtract(doneNumber));

        if (waitAto.getTransactionSurplusNumber().compareTo(BigDecimal.ZERO) > 0) {
            reverseAtoQueue.add(waitAto);
        } else {
            finishOrderIdList.add(waitAto.getId());
        }

        String buyOrderId = buyOrder.getId();
        String sellOrderId = sellOrder.getId();


        //撮合结果日志记录
        CctOrderMatchLog cctOrderMatchLog = new CctOrderMatchLog();
        cctOrderMatchLog.setBuyOrderId(buyOrderId);
        cctOrderMatchLog.setSellOrderId(sellOrderId);
        cctOrderMatchLog.setTradePairId(tradePair.getId());
        cctOrderMatchLog.setDoneUnit(doneUnit);
        cctOrderMatchLog.setDoneNumber(doneNumber);
        cctOrderMatchLog.setCreateTime(new Timestamp(System.currentTimeMillis()));

        handleMatchOrder(cctOrderMatchLog, finishOrderIdList);

        cctKlineService.updateKline(tradePair, doneUnit, doneNumber);
    }


    //撮合交易处理
    public void handleMatchOrder(CctOrderMatchLog cctOrderMatchLog, List<String> finishOrderIdList) {
        String tradePairId = cctOrderMatchLog.getTradePairId();
        BigDecimal doneUnit = cctOrderMatchLog.getDoneUnit();
        BigDecimal doneNumber = cctOrderMatchLog.getDoneNumber();
        String buyOrderId = cctOrderMatchLog.getBuyOrderId();
        String sellOrderId = cctOrderMatchLog.getSellOrderId();
        String buyerUserId = cctOrderMatchLog.getBuyUserId();
        String sellerUserId = cctOrderMatchLog.getBuyUserId();

        TradePair tradePair = (TradePair) redisClient.getHashKey(RedisKeyEnum.TradePair.getKey(), tradePairId);

        //手续费比例
        BigDecimal buyPoundageRate = new BigDecimal(redisClient.get(RedisKeyEnum.CctTradePoundageRate.getKey())).divide(BigDecimal.valueOf(100));//买入手续费比例
        BigDecimal sellPoundageRate = new BigDecimal(redisClient.get(RedisKeyEnum.CctTradePoundageRate.getKey())).divide(BigDecimal.valueOf(100));//卖出手续费比例


        transactionTemplate.execute(new TransactionCallbackWithoutResult() {
            @Override
            protected void doInTransactionWithoutResult(TransactionStatus transactionStatus) {
                //买方获得资产数目 = 卖方扣除资产数目 * 手续费
                BigDecimal buyerGetAssetsPoundage = doneNumber.multiply(buyPoundageRate);//手续费
                BigDecimal buyerGetAssets = doneNumber.subtract(buyerGetAssetsPoundage); //实际到账

                String tradeCoinId = tradePair.getTradeCoinId();

                ValueCheckUtils.notZero(userCoinAssetsService.increaseUserCoinAssets(buyerUserId, buyerGetAssets, tradeCoinId), "买家资产处理异常"); //资产处理
                ValueCheckUtils.notZero(transactionOrderDetailService.save(buyOrderId, tradePair, doneUnit, doneNumber, buyerGetAssetsPoundage), "资产处理异常");//订单成交记录


                //卖方获得资产数目 = 买方扣除资产数目 *  手续费
                BigDecimal sellerGetAssets = doneUnit.multiply(doneNumber);
                BigDecimal sellerGetAssetsPoundage = sellerGetAssets.multiply(sellPoundageRate); //手续费
                sellerGetAssets = sellerGetAssets.subtract(sellerGetAssetsPoundage); //实际到账

                String valuationCoinId = tradePair.getValuationCoinId();

                ValueCheckUtils.notZero(userCoinAssetsService.increaseUserCoinAssets(sellerUserId, sellerGetAssets, valuationCoinId), "卖家资产处理异常"); //资产处理
                ValueCheckUtils.notZero(transactionOrderDetailService.save(sellOrderId, tradePair, doneUnit, doneNumber, sellerGetAssetsPoundage), "资产处理异常");//订单成交记录

                //扣除冻结资产
                ValueCheckUtils.notZero(userFrozenCoinAssetsService.decreaseUserCoinAssets(buyOrderId, sellerGetAssets, tradePair.getValuationCoinId()), "扣除买家冻结资产失败");        //买家冻结资产扣除
                ValueCheckUtils.notZero(userFrozenCoinAssetsService.decreaseUserCoinAssets(sellOrderId, buyerGetAssets, tradePair.getTradeCoinId()), "扣除卖家冻结资产失败");        //卖家冻结资产扣除


                //扣除委托订单的剩余数量
                ValueCheckUtils.notZero(decreaseOrderSurplusNumber(buyOrderId, doneNumber, finishOrderIdList), "扣除买单剩余数量失败");
                ValueCheckUtils.notZero(decreaseOrderSurplusNumber(sellOrderId, doneNumber, finishOrderIdList), "扣除卖单剩余数量失败");
            }
        });


    }

    //扣除订单的剩余数量
    public int decreaseOrderSurplusNumber(String orderId, BigDecimal number, List<String> finishIdList) {
        StringBuilder sql = new StringBuilder("UPDATE assets_transaction_order SET transaction_surplus_number = transaction_surplus_number - ?");
        if (finishIdList.contains(orderId)) {
            sql.append(",status = ").append(TransactionEnum.FINISH.getType());
        }
        sql.append("WHERE id = ? AND transaction_surplus_number - ? >= 0 ");

        return jdbcTemplate.update(sql.toString(), ps -> {
            ps.setBigDecimal(1, number);
            ps.setString(2, orderId);
            ps.setBigDecimal(3, number);
        });
    }
}
