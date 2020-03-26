package com.converage.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

/**
 * Created by 旺旺 on 2020/3/18.
 */
@Service
public class UserFrozenCoinAssetsService {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    public int decreaseUserCoinAssets(String userId, BigDecimal amount, String coinId) {
        String sql = "UPDATE user_frozen_assets SET assets_amount = assets_amount - ? WHERE user_id = ? AND assets_amount - ? >=0  AND coin_id = ?";
        return jdbcTemplate.update(sql, ps -> {
            ps.setBigDecimal(1, amount);
            ps.setString(2, userId);
            ps.setBigDecimal(3, amount);
            ps.setString(4, coinId);
        });
    }
}
