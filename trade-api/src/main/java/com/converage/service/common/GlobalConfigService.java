package com.converage.service.common;

import com.converage.architecture.exception.BusinessException;
import com.converage.architecture.service.BaseService;
import com.converage.entity.common.GlobalConfig;
import com.converage.mapper.common.GlobalConfigMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * Created by bint on 2018/8/14.
 */
@Service
public class GlobalConfigService extends BaseService {

    public static Map<String, String> gCfgCache;

    @Autowired
    private GlobalConfigMapper globalConfigMapper;

    @PostConstruct
    private void init() {
//        updateGlobalConfigCache();
    }

    private List<GlobalConfig> getAll() {
        List<GlobalConfig> globalConfigList = selectAll(GlobalConfig.class);

        return globalConfigList;
    }

    public void updateGlobalConfigCache() {
        List<GlobalConfig> cfgList = this.getAll();
        if (cfgList != null) {
            Map<String, String> map = new HashMap<>();
            Iterator<GlobalConfig> it = cfgList.iterator();
            while (it.hasNext()) {
                GlobalConfig globalConfig = it.next();
                map.put(globalConfig.getConfigKey(), globalConfig.getConfigValue());
            }
            gCfgCache = map;
        }
    }

    public void updateGlobalConfigCache(String key, String value) {
        gCfgCache.put(key, value);
    }

    /**
     * 获取配置
     *
     * @param key
     * @return
     */
    public String get(Enum key) {
        // 如果缓存数据为空，则初始化
        if (gCfgCache == null || gCfgCache.size() == 0) {
            updateGlobalConfigCache();
        }
        String value = gCfgCache.get(key.toString());

        return value;
    }

    public double getDouble(Enum key) {
        return Double.parseDouble(get(key));
    }

    public double getInt(Enum key) {
        return Integer.parseInt(get(key));
    }

    public String getByDb(Enum key) {
        return globalConfigMapper.getByDb(key);
    }


    /**
     * 更新配置表
     *
     * @param key
     * @param value
     */
    public Integer updateValue(Enum key, String value) {
        if (key == Enum.ALI_PAY_ACCOUNT_ID
                || key == Enum.ALI_PAY_APP_ID
                || key == Enum.ALI_PAY_PRIVATE_KEY
                || key == Enum.ALI_PAY_PUBLIC_KEY
                || key == Enum.ALI_PAY_RSA_PUBLIC_KEY
                || key == Enum.APP_DOMAIN_NAME
                || key == Enum.APP_WEIXIN_APP_ID
                || key == Enum.APP_WEIXIN_APP_SECRET
                ) {
            throw new BusinessException("此项数据不可修改");
        }


        updateGlobalConfigCache(key.toString(), value);
        return globalConfigMapper.updateValue(key, value);
    }


    public enum Enum {
        //阿里云OSS配置
        OSS_ACCESS_KEY_ID,
        OSS_ACCESS_KEY_SECRET,
        OSS_END_POINT,
        OSS_COMMON_BUCKET_NAME,
        OSS_SENSITIVE_BUCKET_NAME,

        OSS_APP_CLIENT_BUCKET_NAME,//app客户端bucket

        MINING_REWARD_STOLEN_RATE,//

        //系统域名
        SYSTEM_DOMAIN,

        WECHAT_MCH_KEY,//微信商户Key
        WECHAT_MCH_ID,//微信商户Id
        APP_WEIXIN_APP_ID,//app微信appID
        APP_WEIXIN_APP_SECRET,//app微信appSecret

        ALI_PAY_ACCOUNT_ID,//支付宝账号id
        ALI_PAY_APP_ID,//支付宝APPID
        ALI_PAY_PUBLIC_KEY,//支付宝公钥
        ALI_PAY_PRIVATE_KEY,//支付宝私钥
        ALI_PAY_RSA_PUBLIC_KEY,//支付宝应用公钥

        /**
         * 每天分享最多次数
         */
        DAILY_SHARE_TIME,


        //APP域名
        APP_DOMAIN_NAME,

        DIRECT_COMPUTE_REWARD,//直接邀请品值奖励

        INDIRECT_COMPUTE_REWARD,//间接邀请品值奖励

        MINING_SHARE_PROFIT_FLAG,//收益分销开关

        OFFICIAL_USDT_WALLETADDRESS,//USDT钱包显示地址
        OFFICIAL_USDT_WALLETADDRESS_LIST,//USDT钱包切换地址

        OFFICIAL_CURRENCY_WALLETADDRESS,//官方平台币显示地址
        OFFICIAL_CURRENCY_WALLETADDRESS_LIST,//官方平台币地址

        WJ_SMS_UID, WJ_SMS_KEY,//网建UID,KEY

        BUY_NUMBER_MIN_LIMIT,//买入最低数量
        BUY_NUMBER_MAX_LIMIT,//买入最高数量
        BUY_UNIT_MIN_LIMIT,//买入最低价格
        BUY_UNIT_MAX_LIMIT,//买入最高价格

        SELL_NUMBER_MIN_LIMIT,//卖出最低数量
        SELL_NUMBER_MAX_LIMIT,//卖出最高数量
        SELL_UNIT_MIN_LIMIT,//卖出最低价格
        SELL_UNIT_MAX_LIMIT,//卖出最低价格

        TRANSACTION_BUY_POUNDAGE_RATE,//买入手续费比例
        TRANSACTION_SELL_POUNDAGE_RATE,//卖出手续费比例

        QRCODE_FILE_DIR,//二维码临时存放文件夹
        QRCODE_ZIP_DIR,//二维码压缩文件夹

        QRCODE_ZIP_PATH,//二维码压缩文件夹路径

        SIGN_RULER,//签到规则
        INVITE_RULER, //邀请规则
        DAILY_SIGN_REWARD,//每日签到奖励
        WEEKLY_SIGN_REWARD,//每周签到奖励
        MONTH_SIGN_REWARD,//每月签到奖励

        OFFICE_SUB_CODE,//官方公众号兑换码
        OFFICE_REWARD_CODE,//官方社群兑换码

        REDIRECT_AFTER_REGISTER,//注册链接

        JPUSH_SECRET,//激光推送master密钥
        JPUSH_KEY,//激光推送app密钥

        USDT_CNY_PRICE,//USDT人民幣價格
        COMPUTE_CNY_PRICE,//品值價格

        PASS_IP,
        TRANSFER_RULER,

        Distribute_Transfer_Free_Address,//分配转账手续费地址
        Distribute_Transfer_Free_Private_Key,//分配转账手续费地址密钥
        Distribute_Transfer_Free_Amount,//分配转账手续费数目


        Merge_Transfer_Free_Address,//归集充值资产
        Merge_Transfer_Free_Private_Key,//归集充值资产密钥


        Withdraw_Output_Address,//提现转出地址
        Withdraw_Output_Private_Key,//提现转出地址密钥


        WALLET_KEY,//ETH钱包部分密码
    }

    void switchUsdtWalletAddress() {
        String[] addressArr = getByDb(Enum.OFFICIAL_USDT_WALLETADDRESS_LIST).split(",");
        if (addressArr.length > 0) {
            Integer length = addressArr.length;
            String viewAddress = getByDb(Enum.OFFICIAL_USDT_WALLETADDRESS);
            for (int i = 0; i < length; i++) {
                if (addressArr[i].equals(viewAddress)) {
                    String newAddress;
                    if (i + 1 == length) {
                        newAddress = addressArr[0];
                    } else {
                        newAddress = addressArr[i + 1];
                    }
                    updateValue(Enum.OFFICIAL_USDT_WALLETADDRESS, newAddress);
                    break;
                }
            }
        }
    }

    void switchCurrencyWalletAddress() {
        String[] addressArr = getByDb(Enum.OFFICIAL_CURRENCY_WALLETADDRESS_LIST).split(",");
        if (addressArr.length > 0) {
            Integer length = addressArr.length;
            String viewAddress = getByDb(Enum.OFFICIAL_CURRENCY_WALLETADDRESS);
            for (int i = 0; i < length; i++) {
                if (addressArr[i].equals(viewAddress)) {
                    String newAddress;
                    if (i + 1 == length) {
                        newAddress = addressArr[0];
                    } else {
                        newAddress = addressArr[i + 1];
                    }
                    updateValue(Enum.OFFICIAL_CURRENCY_WALLETADDRESS, newAddress);
                    break;
                }
            }
        }
    }

}
