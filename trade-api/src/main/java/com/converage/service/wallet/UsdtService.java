package com.converage.service.wallet;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.converage.architecture.service.BaseService;
import com.converage.entity.chain.MainNetUserAddr;
import com.converage.entity.chain.WalletConfig;
import com.converage.exception.CoinException;
import com.converage.exception.WalletException;
import com.converage.init.WalletConfigInit;
import com.converage.utils.HttpUtils;
import com.google.common.collect.Lists;
import com.googlecode.jsonrpc4j.JsonRpcHttpClient;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.collections.map.HashedMap;
import org.apache.commons.lang.StringUtils;
import org.bitcoinj.core.*;
import org.bitcoinj.crypto.TransactionSignature;
import org.bitcoinj.params.MainNetParams;
import org.bitcoinj.params.TestNet3Params;
import org.bitcoinj.script.Script;
import org.bitcoinj.script.ScriptBuilder;
import org.bouncycastle.util.encoders.Hex;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.*;

/**
 * Created by 旺旺 on 2020/3/26.
 */

@Service
@Slf4j
public class UsdtService extends BaseService {

    private static final String mainAddress = "xxx";//手续费地址
    private final static String RESULT = "result";
    private final static String METHOD_GET_BLOCK = "getblock";
    private final static String METHOD_GET_BLOCK_HASH = "getblockhash";
    private final static String METHOD_GET_TRANSACTION = "omni_gettransaction";
    private final static String METHOD_GET_BLOCK_COUNT = "getblockcount";
    private final static String METHOD_NEW_ADDRESS = "getnewaddress";
    private final static String METHOD_GET_BALANCE = "omni_getbalance";
    private final static String METHOD_GET_LISTUNSPENT = "listunspent";
    private final static String DUMP_PRIVATE = "dumpprivkey";
    private final static int MIN_CONFIRMATION = 6;

    //正式网络usdt=31，测试网络可以用2
    private static final int propertyid = 2;
    public Boolean isMainNet = false;

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
            log.error("获取USDT余额:{}", json.get("error"));
            return 0;
        }
        return json.getDouble(RESULT);
    }

    /**
     * BTC获取私钥
     */
    public String getPrivateAddress(String address) {
        JSONObject json = doRequest(DUMP_PRIVATE, address);
        if (isError(json)) {
            log.error("获取USDT地址失败:{}" + json.get("error"));
            return "";
        }
        return json.getString(RESULT);

    }

    /**
     * usdt 离线签名
     *
     * @param toAddress：接收地址
     * @param amount:转账金额
     * @return
     */
    public String rawSignAndSend(String fromAddress, String toAddress, String changeAddress, Long amount) throws Exception {
        List<UTXO> utxos;
        List<UTXO> utxoss;
        if (mainAddress.equals(fromAddress)) {
            utxos = this.getUnspents(fromAddress);

        } else {
            utxos = this.getUnspents(fromAddress);
            utxoss = this.getUnspents(toAddress);
            for (int i = 0; i < utxoss.size(); i++) {
                utxos.add(utxoss.get(i));
            }
        }
        System.out.println(utxos);
        // 获取手续费
        Long fee = this.getOmniFee(utxos);
        //判断是主链试试测试链
        NetworkParameters networkParameters = isMainNet ? MainNetParams.get() : TestNet3Params.get();
        Transaction tran = new Transaction(networkParameters);
        if (utxos == null || utxos.size() == 0) {
            throw new Exception("utxo为空");
        }
        //这是比特币的限制最小转账金额，所以很多usdt转账会收到一笔0.00000546的btc
        Long miniBtc = 546L;
        tran.addOutput(Coin.valueOf(miniBtc), Address.fromBase58(networkParameters, toAddress));

        //构建usdt的输出脚本 注意这里的金额是要乘10的8次方
        String usdtHex = "6a146f6d6e69" + String.format("%016x", propertyid) + String.format("%016x", amount);
        tran.addOutput(Coin.valueOf(0L), new Script(Utils.HEX.decode(usdtHex)));

        Long changeAmount = 0L;
        Long utxoAmount = 0L;
        List<UTXO> needUtxo = new ArrayList<>();
        //过滤掉多的uxto
        for (UTXO utxo : utxos) {
            if (utxoAmount > (fee + miniBtc)) {
                break;
            } else {
                needUtxo.add(utxo);
                utxoAmount += utxo.getValue().value;
            }
        }
        changeAmount = utxoAmount - (fee + miniBtc);
        //余额判断
        if (changeAmount < 0) {
            throw new Exception("utxo余额不足");
        }
        if (changeAmount > 0) {
            tran.addOutput(Coin.valueOf(changeAmount), Address.fromBase58(networkParameters, changeAddress));
        }

        //先添加未签名的输入，也就是utxo
        for (UTXO utxo : needUtxo) {
            tran.addInput(utxo.getHash(), utxo.getIndex(), utxo.getScript()).setSequenceNumber(TransactionInput.NO_SEQUENCE - 2);
        }


        //下面就是签名
        for (int i = 0; i < needUtxo.size(); i++) {
            //这里获取地址
            String addr = needUtxo.get(i).getAddress();
            String privateKeys = this.getPrivateAddress(addr);

            ECKey ecKey = DumpedPrivateKey.fromBase58(networkParameters, privateKeys).getKey();
            TransactionInput transactionInput = tran.getInput(i);
            Script scriptPubKey = ScriptBuilder.createOutputScript(Address.fromBase58(networkParameters, addr));
            Sha256Hash hash = tran.hashForSignature(i, scriptPubKey, Transaction.SigHash.ALL, false);
            ECKey.ECDSASignature ecSig = ecKey.sign(hash);
            TransactionSignature txSig = new TransactionSignature(ecSig, Transaction.SigHash.ALL, false);
            transactionInput.setScriptSig(ScriptBuilder.createInputScript(txSig, ecKey));
        }

        //这是签名之后的原始交易，直接去广播就行了
        String signedHex = Hex.toHexString(tran.bitcoinSerialize());
        log.info("签名之后的原始交易:{}" + signedHex);
        //这是交易的hash
        String txHash = Hex.toHexString(Utils.reverseBytes(Sha256Hash.hash(Sha256Hash.hash(tran.bitcoinSerialize()))));
        log.info("fee:{},utxoAmount:{},changeAmount:{}", fee, utxoAmount, changeAmount, txHash);

        JSONObject json = doRequest("sendrawtransaction", signedHex);
        if (isError(json)) {
            log.error("发送交易失败");
            return null;
        } else {
            String result = json.getString("result");
            log.info("发送成功 hash:{}", result);
            return result;
        }

    }

    /**
     * 获取矿工费用
     *
     * @param utxos
     * @return
     */
    public Long getOmniFee(List<UTXO> utxos) {
        Long miniBtc = 546L;
        Long feeRate = getFeeRate();
        Long utxoAmount = 0L;
        Long fee = 0L;
        Long utxoSize = 0L;
        for (UTXO output : utxos) {
            utxoSize++;
            if (utxoAmount > (fee + miniBtc)) {
                break;
            } else {
                utxoAmount += output.getValue().value;
                fee = (utxoSize * 148 + 34 * 2 + 10) * feeRate;
            }
        }
        return fee;
    }


    public List<UTXO> getUnspents(String... address) {
        List<UTXO> utxos = Lists.newArrayList();

        try {
            JSONObject jsonObject = doRequest(METHOD_GET_LISTUNSPENT, 0, 99999999, address);
            JSONArray outputs = jsonObject.getJSONArray("result");
            if (outputs == null || outputs.size() == 0) {
                System.out.println("交易异常，余额不足");
            }
            for (int i = 0; i < outputs.size(); i++) {
                JSONObject outputsMap = outputs.getJSONObject(i);
                String txid = outputsMap.get("txid").toString();
                String vout = outputsMap.get("vout").toString();
                String addr = outputsMap.get("address").toString();
                String script = outputsMap.get("scriptPubKey").toString();
                String amount = outputsMap.get("amount").toString();
                BigDecimal bigDecimal = new BigDecimal(amount);
                bigDecimal = bigDecimal.multiply(new BigDecimal(100000000));
                // String confirmations = outputsMap.get("confirmations").toString();
                UTXO utxo = new UTXO(Sha256Hash.wrap(txid), Long.valueOf(vout), Coin.valueOf(bigDecimal.longValue()),
                        0, false, new Script(Hex.decode(script)), addr);
                System.out.println(utxo.getAddress());
                utxos.add(utxo);
            }
            return utxos;
        } catch (Exception e) {
            log.error("【BTC获取未消费列表】失败，", e);
            return null;
        }

    }

    /**
     * 获取btc费率
     *
     * @return
     */
    public Long getFeeRate() {
        try {
            String httpGet1 = HttpUtils.doGet("https://bitcoinfees.earn.com/api/v1/fees/recommended").toString();
            Map map = JSON.parseObject(httpGet1, Map.class);
            Long fastestFee = Long.valueOf(map.get("fastestFee").toString());
            return fastestFee;
        } catch (Exception e) {
            e.printStackTrace();
            return 0L;
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
            log.error("访问USDT出错");
            return false;
        }
        String hash = jsonBlockHash.getString(RESULT);
        JSONObject jsonBlock = doRequest(METHOD_GET_BLOCK, hash);
        if (isError(jsonBlock)) {
            log.error("访问USDT出错");
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
        WalletConfig walletConfig = WalletConfigInit.map.get(WalletConfig.USDT);
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
}
