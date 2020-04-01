package com.converage;

import com.converage.architecture.service.BaseService;
import com.converage.entity.chain.MainNetUserAddr;
import com.converage.entity.user.User;
import com.converage.service.user.UserService;
import com.converage.utils.AesUtils;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.web.WebAppConfiguration;

/**
 * Created by 旺旺 on 2020/3/26.
 */
@SpringBootTest
public class UserTest {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private BaseService baseService;

    @Test
    public void test() {
    }
}
