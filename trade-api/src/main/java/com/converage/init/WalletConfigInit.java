package com.converage.init;

import com.converage.architecture.service.BaseService;
import com.converage.entity.chain.WalletConfig;
import org.apache.commons.collections.map.HashedMap;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.List;
import java.util.Map;

/**
 * Created by 旺旺 on 2020/3/26.
 */
@Component
public class WalletConfigInit {

    public static Map<String, WalletConfig> map = new HashedMap();

    @Autowired
    private BaseService baseService;

    @PostConstruct
    public void init() {
        List<WalletConfig> list = baseService.selectAll(WalletConfig.class);
        for (WalletConfig walletConfig : list) {
            map.put(walletConfig.getName(), walletConfig);
        }
    }
}
