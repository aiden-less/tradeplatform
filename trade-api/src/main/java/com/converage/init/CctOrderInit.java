package com.converage.init;

import com.google.common.collect.ImmutableMap;
import com.converage.architecture.service.BaseService;
import com.converage.constance.TransactionEnum;
import com.converage.entity.transaction.CctOrder;
import com.converage.entity.market.TradePair;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;

import javax.annotation.PostConstruct;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by 旺旺 on 2020/3/17.
 */
@Data
public class CctOrderInit {

    public static ConcurrentHashMap<String, Queue<CctOrder>> cctOrderQueueMap = new ConcurrentHashMap();

    @Autowired
    private BaseService baseService;

    @PostConstruct
    private void init() {
        Map<String, Object> map = ImmutableMap.of(
                TradePair.If_Valid + "=", true
        );
        List<TradePair> activeTradePairList = baseService.selectListByWhereMap(map, TradePair.class);

        activeTradePairList.parallelStream().forEach(tradePair -> {
            String tradePairId = tradePair.getId();
            String tradePairName = tradePair.getPairName();


            Integer buyType = TransactionEnum.BUY.getType();
            Comparator<CctOrder> buyComparator = buildCmpOnTransactionType(buyType);
            Queue<CctOrder> cctBuyOrderQueue = new PriorityQueue<>(buyComparator);


            Integer sellType = TransactionEnum.SELL.getType();
            Comparator<CctOrder> sellComparator = buildCmpOnTransactionType(sellType);
            Queue<CctOrder> cctSellOrderQueue = new PriorityQueue<>(sellComparator);


            Map<String, Object> whereMap = ImmutableMap.of(
                    CctOrder.Trade_pair_id + "=", tradePairId,
                    CctOrder.Status + "=", TransactionEnum.UN_FINISH.getType()

            );
            List<CctOrder> transactionOrderList = baseService.selectListByWhereMap(whereMap, CctOrder.class);

            for (CctOrder ato : transactionOrderList) {
                if (ato.getTransactionType() == buyType) {
                    cctBuyOrderQueue.add(ato);
                } else {
                    cctSellOrderQueue.add(ato);
                }
            }

            cctOrderQueueMap.put(
                    buildCctOrderQueueMap(tradePairName, buyType),
                    cctBuyOrderQueue
            );

            cctOrderQueueMap.put(
                    buildCctOrderQueueMap(tradePairName, sellType),
                    cctSellOrderQueue
            );

        });
    }


    //根据交易类型创建构造器
    public static Comparator<CctOrder> buildCmpOnTransactionType(Integer transactionType) {
        Comparator<CctOrder> comparator = new Comparator<CctOrder>() {
            @Override
            public int compare(CctOrder o1, CctOrder o2) {
                if (transactionType == 1) { //买入订单委托队列排序
                    int i = o2.getTransactionUnit().compareTo(o1.getTransactionUnit());
                    if (i == 0) {
                        return o1.getCreateTime().compareTo(o2.getCreateTime());
                    } else {
                        return i;
                    }
                } else {//卖出订单委托队列排序
                    int i = o1.getTransactionUnit().compareTo(o2.getTransactionUnit());
                    if (i == 0) {
                        return o1.getCreateTime().compareTo(o2.getCreateTime());
                    } else {
                        return i;
                    }
                }
            }

            @Override
            public boolean equals(Object obj) {
                return false;
            }
        };

        return comparator;
    }

    public static String buildCctOrderQueueMap(String tradePairName, Integer transactionType) {
        String tradeTypeStr = transactionType == TransactionEnum.BUY.getType() ? "BUY" : "SELL";
        return tradePairName + "_" + tradeTypeStr;
    }

    //TODO 测试静态代码
    static {
        String tradePairName = "BTC/USDT";
        Integer buyType = TransactionEnum.SELL.getType();
        Comparator<CctOrder> buyComparator = buildCmpOnTransactionType(buyType);
        Queue<CctOrder> cctBuyOrderQueue = new PriorityQueue<>(buyComparator);

        CctOrder ato1 = new CctOrder();
        ato1.setTransactionUnit(BigDecimal.valueOf(7500));
        ato1.setCreateTime(new Timestamp(1584497963122L));
        cctBuyOrderQueue.add(ato1);

        CctOrder ato2 = new CctOrder();
        ato2.setTransactionUnit(BigDecimal.valueOf(7500));
        ato2.setCreateTime(new Timestamp(1584497963123L));
        cctBuyOrderQueue.add(ato2);

        cctOrderQueueMap.put(
                buildCctOrderQueueMap(tradePairName, buyType),
                cctBuyOrderQueue
        );


    }

    public static void main(String[] args) {


    }


}
