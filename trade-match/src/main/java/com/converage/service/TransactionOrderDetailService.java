package com.converage.service;

import com.converage.constance.UUIDUtils;
import com.converage.entity.TradePair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.UUID;

/**
 * Created by 旺旺 on 2020/3/18.
 */
@Service
public class TransactionOrderDetailService {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    public int save(String orderId, TradePair tradePair, BigDecimal doneUnit, BigDecimal doneNumber, BigDecimal buyerGetAssetsPoundage) {
        String sql = "INSERT INTO cct_order_detail " +
                "('id','order_id','trade_coin_name','valuation_coin_name','transaction_unit','transaction_number','transaction_poundage','create_time')" +
                "VALUES" +
                "(?,?,?,?,?,?,?,?)";
        return jdbcTemplate.update(sql, ps -> {
            ps.setString(1, UUIDUtils.createUUID());
            ps.setString(2, orderId);
            ps.setString(3, tradePair.getTradeCoinName());
            ps.setString(4, tradePair.getValuationCoinName());
            ps.setBigDecimal(5, doneUnit);
            ps.setBigDecimal(6, doneNumber);
            ps.setBigDecimal(7, buyerGetAssetsPoundage);
            ps.setTimestamp(8, new Timestamp(System.currentTimeMillis()));
        });
    }
}
