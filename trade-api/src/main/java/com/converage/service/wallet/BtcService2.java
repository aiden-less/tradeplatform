package com.converage.service.wallet;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.converage.architecture.exception.BusinessException;
import com.converage.architecture.service.BaseService;
import com.converage.entity.chain.WalletConfig;
import com.converage.init.WalletConfigInit;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import wf.bitcoin.javabitcoindrpcclient.BitcoinJSONRPCClient;

import java.math.BigDecimal;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by 旺旺 on 2020/3/26.
 */
@Service
@Slf4j
public class BtcService2 extends BaseService {

    public String createBtcAddress(String walletName, String walletPassword, String addressName) {
        try {
            //获取BTC客户端默认钱包
            BitcoinJSONRPCClient bitcoinClient = getBitcoinClient("");
            //创建钱包
            String walletResult = bitcoinClient.query("createwallet", walletName).toString();
            log.info("钱包(" + walletName + ")创建成功返回==>" + walletResult + "");
            //获取当前BTC客户端钱包
            bitcoinClient = getBitcoinClient(walletName);
            //设置钱包密码
            bitcoinClient.encryptWallet(walletPassword);
            //创建地址
            return bitcoinClient.getNewAddress(addressName);
        } catch (Exception e) {
            log.error("创建钱包地址异常==>" + e.getMessage());
        }
        return null;
    }


    public double getBtcBalance(String walletName, String address) {
        try {
            List<JSONArray> jsonArrayList = getListAddressInfo(walletName);
            for (JSONArray jsonArray : jsonArrayList) {
                if (jsonArray.getString(0).equals(address)) {
                    return jsonArray.getDoubleValue(1);
                }
            }
        } catch (Exception e) {
            log.error("钱包(" + walletName + ")地址(" + address + ")获取余额异常==>" + e.getMessage());
        }
        return 0;
    }


    public void transfetBtc(String walletName, String walletPassword, String toAddress, double quantity) {
        try {
            BitcoinJSONRPCClient bitcoinClient = getBitcoinClient(walletName);
            //获取钱包余额
            double balance = bitcoinClient.getBalance().doubleValue();
            if (balance < quantity) {
                throw new BusinessException("余额不足");
            } else {
                //输入钱包密码
                bitcoinClient.walletPassPhrase(walletPassword, 60);
                //开始转账
                String result = bitcoinClient.sendToAddress(toAddress, BigDecimal.valueOf(quantity));
                log.info("钱包(" + walletName + ")成功转账" + quantity + "个btc到" + toAddress + "交易hash==>" + result);

            }
        } catch (Exception e) {
            log.error("钱包(" + walletName + ")转账" + quantity + "个btc到" + toAddress + "异常==>" + e.getMessage());
        }
    }


    public String getBtcPrivateKey(String walletName, String walletPassword, String address) {
        try {
            BitcoinJSONRPCClient bitcoinClient = getBitcoinClient(walletName);
            bitcoinClient.walletPassPhrase(walletPassword, 60);
            Object addressPrivate = bitcoinClient.query("dumpprivkey", address);
            return addressPrivate.toString();
        } catch (Exception e) {
            log.error("获取地址(" + address + ")私钥异常==>" + e.getMessage());
        }
        return null;
    }


    public boolean importBtcPrivateKey(String walletName, String walletPassword, String privateKey) {
        try {
            BitcoinJSONRPCClient bitcoinClient = getBitcoinClient(walletName);
            bitcoinClient.walletPassPhrase(walletPassword, 60);
            bitcoinClient.importPrivKey(privateKey);
            return true;
        } catch (Exception e) {
            log.error("导入地址私钥(" + privateKey.substring(10) + "******)到钱包(" + walletName + ")异常==>" + e.getMessage());
        }
        return false;
    }

    /**
     * 获取BTC客户端
     *
     * @param walletName 钱包名称
     * @return BTC客户端
     */
    private BitcoinJSONRPCClient getBitcoinClient(String walletName) {
        WalletConfig walletConfig = WalletConfigInit.map.get(WalletConfig.BTC);
        String user = walletConfig.getUser();
        String password = walletConfig.getPassword();
        String host = walletConfig.getHost();
        String port = walletConfig.getPort();
        BitcoinJSONRPCClient bitcoinClient = null;
        try {
            URL url = new URL("http://" + user + ':' + password + "@" + host + ":" + port + "/wallet/" + walletName + "");
            bitcoinClient = new BitcoinJSONRPCClient(url);
        } catch (MalformedURLException e) {
            log.error("获取BTC RPC错误==>" + e.getMessage());
        }
        return bitcoinClient;
    }

    /**
     * 获取所有钱包地址信息
     *
     * @param walletName 钱包名称
     * @return 所有地址信息
     */
    private List<JSONArray> getListAddressInfo(String walletName) {
        BitcoinJSONRPCClient bitcoinClient = getBitcoinClient(walletName);
        List<JSONArray> resultList = new ArrayList<JSONArray>();
        try {
            Object walletAddressAll = bitcoinClient.query("listaddressgroupings");
            JSONArray jsonArrayAll = JSON.parseArray(JSON.toJSONString(walletAddressAll));
            for (int i = 0; i < jsonArrayAll.size(); i++) {
                JSONArray walletArray = JSON.parseArray(jsonArrayAll.get(i).toString());
                for (int j = 0; j < walletArray.size(); j++) {
                    JSONArray jsonArray = JSON.parseArray(walletArray.getString(j));
                    resultList.add(jsonArray);
                }
            }
        } catch (Exception e) {
            log.error("钱包(" + walletName + ")获取所有地址信息异常==>" + e.getMessage());
        }
        return resultList;
    }
}
