package com.converage.init;


import com.converage.constance.TransactionEnum;
import com.converage.entity.TradePair;
import lombok.Data;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import com.converage.entity.CctOrder;

import javax.annotation.PostConstruct;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by 旺旺 on 2020/3/17.
 */
@Data
public class CctOrderInit {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    public static ConcurrentHashMap<String, Queue<CctOrder>> cctOrderQueueMap = new ConcurrentHashMap();

    @PostConstruct
    private void init() {
        List<TradePair> activeTradePairList = listActiveTradePair();

        activeTradePairList.parallelStream().forEach(tradePair -> {
            String tradePairId = tradePair.getId();
            String tradePairName = tradePair.getPairName();

            Integer buyType = TransactionEnum.BUY.getType();
            Comparator<CctOrder> buyComparator = buildCmpOnTransactionType(buyType);
            Queue<CctOrder> cctBuyOrderQueue = new PriorityQueue<>(buyComparator);

            Integer sellType = TransactionEnum.SELL.getType();
            Comparator<CctOrder> sellComparator = buildCmpOnTransactionType(sellType);
            Queue<CctOrder> cctSellOrderQueue = new PriorityQueue<>(sellComparator);

            List<CctOrder> transactionOrderList = listAto4TradePair(tradePairId);

            for (CctOrder ato : transactionOrderList) {
                if (ato.getTransactionType() == buyType) {
                    cctBuyOrderQueue.add(ato);
                } else {
                    cctSellOrderQueue.add(ato);
                }
            }

            cctOrderQueueMap.put(
                    buildCctOrderQueueKey(tradePairName, buyType),
                    cctBuyOrderQueue
            );

            cctOrderQueueMap.put(
                    buildCctOrderQueueKey(tradePairName, sellType),
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

    public static String buildCctOrderQueueKey(String tradePairName, Integer transactionType) {
        String tradeTypeStr = transactionType == TransactionEnum.BUY.getType() ? "BUY" : "SELL";
        return tradePairName + "_" + tradeTypeStr;
    }


    //获取上架的交易对
    public List<TradePair> listActiveTradePair() {
        String sql = "select * from trade_pair where if_valid = true";
        final List<TradePair> tradePairList = new ArrayList<>();
        jdbcTemplate.query(sql, new Object[]{}, resultSet -> {
            TradePair tradePair = new TradePair();
            BeanUtils.copyProperties(resultSet, tradePair);
            tradePairList.add(tradePair);
        });
        return tradePairList;
    }

    //获取交易对相应的订单列表
    public List<CctOrder> listAto4TradePair(String tradePairId) {
        String sql = "select * from assets_transaction_order where trade_pair_id = ? and status = ?";
        final List<CctOrder> atoList = new ArrayList<>();
        jdbcTemplate.query(sql, new Object[]{tradePairId, TransactionEnum.UN_FINISH.getType()}, resultSet -> {
            CctOrder ato = new CctOrder();
            atoList.add(ato);
        });
        return atoList;
    }

}
