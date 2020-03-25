package com.converage.service.common;


import com.converage.architecture.service.BaseService;
import com.converage.service.transaction.CctService;
import com.converage.service.currency.HuobiService;
import com.converage.service.user.*;
import com.converage.service.wallet.EthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.concurrent.ExecutionException;

@Component
public class ScheduledService {

    @Autowired
    private GlobalConfigService globalConfigService;

    @Autowired
    private UserService userService;

    @Autowired
    private BaseService baseService;

    @Autowired
    private HuobiService huobiService;

    @Autowired
    private CctService cctService;

    @Autowired
    private EthService ethService;


    //初始化发送短信次数
    @Scheduled(cron = "0/60 * * * * ?")
    public void everyMinute() {
//        CacheUtils.initSmsCountMap();
//        CacheUtils.initWithdrawCountMap();
//        CacheUtils.initRechargeCountMap();
    }



    @Scheduled(cron = "0 */5 * * * ?")
    public void scanETHWalletTrxRecord() throws IOException {
//        ethService.syncEthBlock();
    }

    @Scheduled(cron = "0 */6 * * * ?")
    public void distributePoundage4EthTran() throws IOException, ExecutionException, InterruptedException {
//        ethService.distributePoundage4EthTran();
//        ethService.mergeRecharge();
    }



}

