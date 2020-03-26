package com.converage.service.wallet;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.aliyun.oss.common.utils.HttpUtil;
import com.converage.architecture.service.BaseService;
import com.converage.entity.chain.MainNetUserAddr;
import com.converage.entity.chain.WalletConfig;
import com.converage.init.WalletConfigInit;
import com.converage.utils.HttpUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.collections.map.HashedMap;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.*;

/**
 * Created by 旺旺 on 2020/3/26.
 */

@Service
@Slf4j
public class BtcService extends BaseService {

    private final static String RESULT = "result";
    private final static String METHOD_SEND_TO_ADDRESS = "sendtoaddress";
    private final static String METHOD_GET_BLOCK = "getblock";
    private final static String METHOD_GET_BLOCK_HASH = "getblockhash";
    private final static String METHOD_GET_TRANSACTION = "gettransaction";
    private final static String METHOD_GET_BLOCK_COUNT = "getblockcount";
    private final static String METHOD_NEW_ADDRESS = "getnewaddress";
    private final static String METHOD_GET_BALANCE = "getbalance";
    private final static int MIN_CONFIRMATION = 6;

    //前四个参数在BTC钱包conf文件中设置
    //钱包密码PASSWORD打开钱包后设置的密码

    /***
     * 取得钱包相关信息
     * 若获取失败，result为空，error信息为错误信息的编码
     * */
    public JSONObject getInfo() throws Exception {
        return doRequest("getinfo");
    }

    /**
     * 获取块链信息
     *
     * @return
     * @throws Exception
     */
    public JSONObject getBlockChainInfo() throws Exception {
        return doRequest("getblockchaininfo");
    }

    /**
     * BTC产生地址
     *
     * @return
     */
    public String getNewAddress() {
        JSONObject json = doRequest(METHOD_NEW_ADDRESS);
        if (isError(json)) {
            log.error("获取BTC地址失败:{}", json.get("error"));
            return "";
        }
        return json.getString(RESULT);
    }

    /**
     * BTC查询余额
     *
     * @return
     */
    public double getBalance() {
        JSONObject json = doRequest(METHOD_GET_BALANCE);
        if (isError(json)) {
            log.error("获取BTC余额:{}", json.get("error"));
            return 0;
        }
        return json.getDouble(RESULT);
    }

    /**
     * BTC转帐
     *
     * @param addr
     * @param value
     * @return
     */
    public String send(String addr, double value) {
        if (vailedAddress(addr)) {
            JSONObject json = doRequest(METHOD_SEND_TO_ADDRESS, addr, value);
            if (isError(json)) {
                log.error("BTC 转帐给{} value:{} 失败 ：", addr, value, json.get("error"));
                return "";
            } else {
                log.info("BTC 转币给{} value:{} 成功", addr, value);
                return json.getString(RESULT);
            }
        } else {
            log.error("BTC接受地址不正确");
            return "";
        }
    }

    /**
     * 验证地址的有效性
     *
     * @param address
     * @return
     * @throws Exception
     */
    public boolean vailedAddress(String address) {
        JSONObject json = doRequest("validateaddress", address);
        if (isError(json)) {
            log.error("BTC验证地址失败:", json.get("error"));
            return false;
        } else {
            return json.getJSONObject(RESULT).getBoolean("isvalid");
        }
    }


    /**
     * 区块高度
     *
     * @return
     */
    public int getBlockCount() {
        JSONObject json = null;
        try {
            json = doRequest(METHOD_GET_BLOCK_COUNT);
            if (!isError(json)) {
                return json.getInteger("result");
            } else {
                log.error(json.toString());
                return 0;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }

    public boolean parseBlock(int index) {
        JSONObject jsonBlockHash = doRequest(METHOD_GET_BLOCK_HASH, index);
        if (isError(jsonBlockHash)) {
            log.error("访问BTC出错");
            return false;
        }
        String hash = jsonBlockHash.getString(RESULT);
        JSONObject jsonBlock = doRequest(METHOD_GET_BLOCK, hash);
        if (isError(jsonBlock)) {
            log.error("访问BTC出错");
            return false;
        }
        JSONObject jsonBlockResult = jsonBlock.getJSONObject(RESULT);
        int confirm = jsonBlockResult.getInteger("confirmations");
        if (confirm >= MIN_CONFIRMATION) {
            JSONArray jsonArrayTx = jsonBlockResult.getJSONArray("tx");
            if (jsonArrayTx == null || jsonArrayTx.size() == 0) {
                //没有交易
                return true;
            }
            Iterator<Object> iteratorTxs = jsonArrayTx.iterator();
            while (iteratorTxs.hasNext()) {
                String txid = (String) iteratorTxs.next();
                parseTx(txid, confirm, null);
            }
            return true;
        } else {
            return false;
        }
    }

    public void parseTx(String txid, int coinfirm, List<MainNetUserAddr> userList) {
        JSONObject jsonTransaction = doRequest(METHOD_GET_TRANSACTION, txid);
        if (isError(jsonTransaction)) {
            //log.error("处理BTC tx出错");
            return;
        }
        JSONObject jsonTransactionResult = jsonTransaction.getJSONObject(RESULT);
        JSONArray jsonArrayVout = jsonTransactionResult.getJSONArray("details");
        if (jsonArrayVout == null || jsonArrayVout.size() == 0) {
            return;
        }
        Iterator<Object> iteratorVout = jsonArrayVout.iterator();
        while (iteratorVout.hasNext()) {
            JSONObject jsonVout = (JSONObject) iteratorVout.next();
            double value = jsonVout.getDouble("amount");
            String category = jsonVout.getString("category");
            if (value > 0 && "receive".equals(category)) {
                String address = jsonVout.getString("address");
                for (MainNetUserAddr userAddr : userList) {
                    //如果有地址是分配给用记的地址， 则说明用户在充值
                    if (address.equals(userAddr.getMainNetAddr())) {
                        //添加充值记录
                        log.info("用户充值");

                    }
                }
            }
        }
    }


    private boolean isError(JSONObject json) {
        if (json == null || (StringUtils.isNotEmpty(json.getString("error")) && json.get("error") != "null")) {
            return true;
        }
        return false;
    }


    private JSONObject doRequest(String method, Object... params) {
        WalletConfig walletConfig = WalletConfigInit.map.get(WalletConfig.BTC);
        String user = walletConfig.getUser();
        String password = walletConfig.getPassword();
        String host = walletConfig.getHost();
        String port = walletConfig.getPort();

        Map<String, Object> param = new HashedMap();
        param.put("id", System.currentTimeMillis() + "");
        param.put("jsonrpc", "2.0");
        param.put("method", method);
        if (params != null) {
            param.put("params", params);
        }
        String creb = Base64.encodeBase64String((user + ":" + password).getBytes());
        Map<String, String> headers = new HashMap<>(2);
        headers.put("Authorization", "Basic " + creb);
        String resp = "";
        if (METHOD_GET_TRANSACTION.equals(method)) {
            try {
                resp = HttpUtils.doPost(host, "", headers, null, param).toString();
            } catch (Exception e) {
                if (e instanceof IOException) {
                    resp = "{}";
                }
            }
        } else {
            try {
                resp = HttpUtils.doPost(host, "", headers, null, param).toString();
            } catch (Exception e) {
                if (e instanceof IOException) {
                    resp = "{}";
                }
            }
            return JSON.parseObject(resp);
        }
        return JSON.parseObject(resp);
    }

    public static void main(String args[]) throws Exception {
    }
}
