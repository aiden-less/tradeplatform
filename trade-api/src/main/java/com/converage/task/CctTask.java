package com.converage.task;

import com.converage.constance.RedisKeyEnum;
import com.converage.service.transaction.CctService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Created by 旺旺 on 2020/4/3.
 */
@Component
public class CctTask {

    @Autowired
    private CctService cctService;

    //币币交易剩余冻结资产退款
    @Scheduled(cron = "0 */1 * * * ?")
    public void refundSurplusFrozenAssets() {

    }
}
