package com.converage.controller.admin;

import com.google.common.collect.Lists;
import com.converage.architecture.dto.Pagination;
import com.converage.architecture.dto.Result;
import com.converage.architecture.exception.BusinessException;
import com.converage.architecture.service.BaseService;
import com.converage.architecture.utils.ResultUtils;
import com.converage.constance.CommonConst;
import com.converage.constance.RedisKeyConst;
import com.converage.entity.assets.WalletInfo;
import com.converage.entity.common.AppUpgrade;
import com.converage.entity.common.GlobalConfig;
import com.converage.entity.user.UserNode;
import com.converage.service.transaction.CctService;
import com.converage.service.common.AppUpgradeService;
import com.converage.service.common.GlobalConfigService;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import com.converage.utils.EnvironmentUtils;
import com.converage.utils.ValueCheckUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.bind.annotation.*;

import java.util.*;

/**
 * 配置相关
 */
@RequestMapping("admin/config")
@RestController
public class AdminConfigController {

    @Autowired
    private BaseService baseService;

    @Autowired
    private GlobalConfigService globalConfigService;

    @Autowired
    private AppUpgradeService appUpgradeService;

    @Autowired
    private StringRedisTemplate stringRedisTemplate;


    @Autowired
    private EnvironmentUtils environmentUtils;

    @Autowired
    private CctService cctService;


    /**
     * 全局配置项 列表
     *
     * @return
     */
    @GetMapping("globalList")
    public Result<?> globalList() {
        List<GlobalConfig> data = baseService.selectAll(GlobalConfig.class);

        List<String> secret = Lists.newArrayList(
                GlobalConfigService.Enum.Merge_Transfer_Free_Address.name(),
                GlobalConfigService.Enum.Merge_Transfer_Free_Private_Key.name(),
                GlobalConfigService.Enum.Distribute_Transfer_Free_Address.name(),
                GlobalConfigService.Enum.Distribute_Transfer_Free_Private_Key.name(),
                GlobalConfigService.Enum.Distribute_Transfer_Free_Amount.name(),
                GlobalConfigService.Enum.Withdraw_Output_Address.name(),
                GlobalConfigService.Enum.Withdraw_Output_Private_Key.name()
        );

        for (GlobalConfig config : data) {
            if (secret.contains(config.getConfigKey())) {
                config.setConfigValue("");
            }
        }


        return ResultUtils.success(data, data.size());
    }

    /**
     * 全局配置项 保存
     *
     * @return
     */
    @PostMapping("globalSave/{operation}")
    public Result<?> globalSave(GlobalConfig globalConfig, @PathVariable String operation) {
        int result;
        String key = globalConfig.getConfigKey();
        String value = globalConfig.getConfigValue();
        if (key.equals(GlobalConfigService.Enum.OFFICIAL_USDT_WALLETADDRESS_LIST)) {
            String[] valueArr = value.split(",");
            ValueCheckUtils.notEmpty(valueArr, "请输入正确格式的地址列表，地址列表格式为用','隔开)");
        }

        if ("update".equals(operation)) {
            result = baseService.updateIfNotNull(globalConfig);
        } else {
            result = baseService.insert(globalConfig);
        }
        if (result > 0) {
            globalConfigService.updateGlobalConfigCache();
            return ResultUtils.success();
        } else {
            return ResultUtils.error("保存失败");
        }
    }




    /**
     * APP更新配置 列表
     */
    @PostMapping("listAppUpgrade")
    public Result<?> listAppUpgrade(@RequestBody Pagination<AppUpgrade> pagination) {
        Map<String, Object> orderMap = ImmutableMap.of(AppUpgrade.Create_time, CommonConst.MYSQL_DESC);
        String device = pagination.getParam().getDevice();
        Boolean state = pagination.getParam().getState();
        Map<String, Object> whereMap = Maps.newHashMap();
        if (device != null) {
            whereMap.put(AppUpgrade.Device + " = ", device);
        }
        if (state != null) {
            whereMap.put(AppUpgrade.State + " = ", state);
        }
        return ResultUtils.success(baseService.selectListByWhereMap(whereMap, pagination, AppUpgrade.class, orderMap), pagination.getTotalRecordNumber());
    }

    /**
     * APP更新配置 保存
     */
    @PostMapping("saveAppUpgrade")
    public Result<?> saveAppUpgrade(AppUpgrade appUpgrade) {
        appUpgradeService.save(appUpgrade);
        return ResultUtils.success();
    }


    /**
     * 操作缓存一些操作
     */
    @PostMapping("cache/{type}")
    public Result<?> cache(String hk, String hv, @PathVariable String type) {
        switch (type) {
            case "common":
                if (StringUtils.isNotBlank(hk) && StringUtils.isNotBlank(hv)) {
                    stringRedisTemplate.opsForHash().put(RedisKeyConst.COMMON_RESOURCE, hk, hv);
                }
                break;
            case "address":

                break;
        }
        return ResultUtils.success();
    }

    /**
     * 运行定时任务
     */
    @PostMapping("runScheduled")
    public Result<?> runScheduled(@RequestParam(defaultValue = "0") int index) {
//        if (!environmentUtils.isTest()) {
//            return ResultUtils.error("未知");
//        }
        switch (index) {
            case 1:
                // 挖矿收益
                try {
                    globalConfigService.updateValue(GlobalConfigService.Enum.MINING_SHARE_PROFIT_FLAG, "1");
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    globalConfigService.updateValue(GlobalConfigService.Enum.MINING_SHARE_PROFIT_FLAG, "0");
                }
                break;
            case 2:
                // 撮合交易
//                transactionOrderService.transactOnQueue(CacheUtils.pollAto());
                break;
            case 3:
                break;
            case 4:

                break;
            default:
                return ResultUtils.error("未知");
        }
        return ResultUtils.success();
    }


    //钱包信息
    @RequestMapping("walletInfo")
    public Result<?> walletInfo() {
        String usdtRechargeAddr = globalConfigService.get(GlobalConfigService.Enum.OFFICIAL_USDT_WALLETADDRESS);
        String usdtRechargeAddrStr = globalConfigService.get(GlobalConfigService.Enum.OFFICIAL_USDT_WALLETADDRESS_LIST);

        String usdtCnyPrice = globalConfigService.get(GlobalConfigService.Enum.USDT_CNY_PRICE);
        String currencyCnyPrice = globalConfigService.get(GlobalConfigService.Enum.COMPUTE_CNY_PRICE);

        String[] strings = usdtRechargeAddrStr.split(",");
        List<String> usdtRechargeAddrSwitchList = Arrays.asList(strings);

        Map<String, Object> orderMap = ImmutableMap.of(UserNode.Reward_percentage, CommonConst.MYSQL_ASC);
        List<UserNode> userNodes = baseService.selectAll(UserNode.class, orderMap);

        Map<String, Object> map = new HashMap<>();
        map.put("usdtRechargeAddr", usdtRechargeAddr);
        map.put("usdtRechargeAddrSwitchList", usdtRechargeAddrSwitchList);
        map.put("usdtCnyPrice", usdtCnyPrice);
        map.put("currencyCnyPrice", currencyCnyPrice);
        map.put("userNodes", userNodes);

        return ResultUtils.success(map);
    }

    //钱包地址切換
    @RequestMapping("walletInfo/switch")
    public Result<?> switchWalletInfo(WalletInfo walletInfo) {
        String usdtWalletAddr = walletInfo.getUsdtWalletAddr();
        if (StringUtils.isEmpty(usdtWalletAddr)) {
            throw new BusinessException("请选择地址");
        }
        globalConfigService.updateValue(GlobalConfigService.Enum.OFFICIAL_USDT_WALLETADDRESS, usdtWalletAddr);
        return ResultUtils.success();
    }

}
