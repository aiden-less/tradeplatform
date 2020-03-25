package rootkey.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

/**
 * Created by 旺旺 on 2020/3/18.
 */
@Service
public class UserCoinAssetsService {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    public int increaseUserCoinAssets(String userId, BigDecimal amount, String coinId) {
        String sql = " UPDATE user_assets SET assets_amount = assets_amount + ? WHERE user_id = ? AND coin_id = ? ";
        return jdbcTemplate.update(sql, ps -> {
            ps.setBigDecimal(1, amount);
            ps.setString(2, userId);
            ps.setString(3, coinId);
        });
    }



}
