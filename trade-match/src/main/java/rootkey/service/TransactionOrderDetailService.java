package rootkey.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import rootkey.entity.TradePair;

import java.math.BigDecimal;
import java.sql.Timestamp;

/**
 * Created by 旺旺 on 2020/3/18.
 */
@Service
public class TransactionOrderDetailService {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    public int save(String orderId, TradePair tradePair, BigDecimal doneUnit, BigDecimal doneNumber, BigDecimal buyerGetAssetsPoundage) {
        String sql = "INSERT INTO cct_order_detail " +
                "('order_id','trade_coin_name','valuation_coin_name','transaction_unit','transaction_number','transaction_poundage','create_time')" +
                "VALUES" +
                "(?,?,?,?,?,?,?)";
        return jdbcTemplate.update(sql, ps -> {
            ps.setString(1, orderId);
            ps.setString(2, tradePair.getTradeCoinName());
            ps.setString(3, tradePair.getValuationCoinName());
            ps.setBigDecimal(4, doneUnit);
            ps.setBigDecimal(5, doneNumber);
            ps.setBigDecimal(6, buyerGetAssetsPoundage);
            ps.setTimestamp(7, new Timestamp(System.currentTimeMillis()));
        });
    }
}
