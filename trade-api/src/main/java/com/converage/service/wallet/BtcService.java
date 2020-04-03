package com.converage.service.wallet;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.converage.architecture.service.BaseService;
import com.converage.entity.assets.CctFinanceLog;
import com.converage.entity.chain.MainNetInfo;
import com.converage.entity.chain.MainNetUserAddr;
import com.converage.entity.chain.WalletConfig;
import com.converage.init.WalletConfigInit;
import com.converage.mapper.user.CctAssetsMapper;
import com.converage.utils.ValueCheckUtils;
import com.googlecode.jsonrpc4j.JsonRpcHttpClient;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallbackWithoutResult;
import org.springframework.transaction.support.TransactionTemplate;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.net.URL;
import java.util.*;

import static com.converage.constance.SettlementConst.*;

/**
 * Created by 旺旺 on 2020/3/26.
 */

@Service
@Slf4j
public class BtcService extends BaseService {

    private static final String mainAddress = "";//手续费地址
    private final static String RESULT = "result";
    private final static String METHOD_SEND_TO_ADDRESS = "sendtoaddress";
    private final static String METHOD_GET_BLOCK = "getblock";
    private final static String METHOD_GET_BLOCK_HASH = "getblockhash";
    private final static String METHOD_GET_TRANSACTION = "gettransaction";
    private final static String METHOD_GET_BLOCK_COUNT = "getblockcount";
    private final static String METHOD_NEW_ADDRESS = "getnewaddress";
    private final static String METHOD_GET_BALANCE = "getbalance";
    private final static String METHOD_GET_LISTUNSPENT = "listunspent";
    private final static String DUMP_PRIVATE = "dumpprivkey";
    private final static int MIN_CONFIRMATION = 6;

    private final static Boolean isMainNet = false;

    @Autowired
    private TransactionTemplate transactionTemplate;

    @Autowired
    private CctAssetsMapper cctAssetsMapper;

    /***
     * 取得钱包相关信息
     * 若获取失败，result为空，error信息为错误信息的编码
     * */
    public Object getInfo() throws Throwable {
        return doRequest("getinfo");
    }

    /**
     * 获取块链信息
     *
     * @return
     * @throws Exception
     */
    public Object getBlockChainInfo() throws Throwable {
        return doRequest("getblockchaininfo");
    }

    /**
     * BTC产生地址
     *
     * @return
     */
    public Object getNewAddress() throws Throwable {
        return doRequest(METHOD_NEW_ADDRESS, new Object());

    }

    /**
     * BTC查询余额
     *
     * @return
     */
    public Object getBalance() throws Throwable {
        return doRequest(METHOD_GET_BALANCE);
    }

    /**
     * BTC获取私钥
     */
    public Object getPrivateAddress(String address) throws Throwable {
        return doRequest(DUMP_PRIVATE, address);

    }


    public Object sendToAddress(String address, double amount) throws Throwable {
        return doRequest(METHOD_SEND_TO_ADDRESS, address, amount);
    }


//    /**
//     * BTC 离线签名
//     *
//     * @param toAddress：接收地址
//     * @param amount:转账金额
//     * @return
//     */
//    public String rawSignAndSend(String fromAddress, String toAddress, String changeAddress, Long amount) throws Throwable {
//        List<UTXO> utxos;
//        List<UTXO> utxoss;
//        if (mainAddress.equals(fromAddress)) {
//            utxos = this.getUnspents(fromAddress);
//
//        } else {
//            utxos = this.getUnspents(fromAddress);
//            utxoss = this.getUnspents(toAddress);
//            utxos.addAll(utxoss);
//        }
//        System.out.println(utxos);
//        // 获取手续费
//        Long fee = this.getOmniFee(utxos);
//        //判断是主链试试测试链
//        NetworkParameters networkParameters = isMainNet ? MainNetParams.get() : TestNet3Params.get();
//        Transaction tran = new Transaction(networkParameters);
//        if (utxos == null || utxos.size() == 0) {
//            throw new Exception("utxo为空");
//        }
//        //这是比特币的限制最小转账金额，所以很多usdt转账会收到一笔0.00000546的btc
//        Long miniBtc = amount;
//        tran.addOutput(Coin.valueOf(miniBtc), Address.fromBase58(networkParameters, toAddress));
//
//        Long changeAmount;
//        Long utxoAmount = 0L;
//        List<UTXO> needUtxo = new ArrayList<>();
//        //过滤掉多的uxto
//        for (UTXO utxo : utxos) {
//            if (utxoAmount > (fee + miniBtc)) {
//                break;
//            } else {
//                needUtxo.add(utxo);
//                utxoAmount += utxo.getValue().value;
//            }
//        }
//        changeAmount = utxoAmount - (fee + miniBtc);
//        //余额判断
//        if (changeAmount < 0) {
//            throw new Exception("utxo余额不足");
//        }
//        if (changeAmount > 0) {
//            tran.addOutput(Coin.valueOf(changeAmount), Address.fromBase58(networkParameters, changeAddress));
//        }
//
//        //先添加未签名的输入，也就是utxo
//        for (UTXO utxo : needUtxo) {
//            tran.addInput(utxo.getHash(), utxo.getIndex(), utxo.getScript()).setSequenceNumber(TransactionInput.NO_SEQUENCE - 2);
//        }
//
//
//        //下面就是签名
//        for (int i = 0; i < needUtxo.size(); i++) {
//            //这里获取地址
//            String addr = needUtxo.get(i).getAddress();
//            String privateKeys = this.getPrivateAddress(addr).toString();
//
//            ECKey ecKey = DumpedPrivateKey.fromBase58(networkParameters, privateKeys).getKey();
//            TransactionInput transactionInput = tran.getInput(i);
//            Script scriptPubKey = ScriptBuilder.createOutputScript(Address.fromBase58(networkParameters, addr));
//            Sha256Hash hash = tran.hashForSignature(i, scriptPubKey, Transaction.SigHash.ALL, false);
//            ECKey.ECDSASignature ecSig = ecKey.sign(hash);
//            TransactionSignature txSig = new TransactionSignature(ecSig, Transaction.SigHash.ALL, false);
//            transactionInput.setScriptSig(ScriptBuilder.createInputScript(txSig, ecKey));
//        }
//
//        //这是签名之后的原始交易，直接去广播就行了
//        String signedHex = Hex.toHexString(tran.bitcoinSerialize());
//        log.info("签名之后的原始交易:{}" + signedHex);
//        //这是交易的hash
//        String txHash = Hex.toHexString(Utils.reverseBytes(Sha256Hash.hash(Sha256Hash.hash(tran.bitcoinSerialize()))));
//        log.info("fee:{},utxoAmount:{},changeAmount:{}", fee, utxoAmount, changeAmount, txHash);
//
//        JSONObject json = JSONObject.parseObject(doRequest("sendrawtransaction", signedHex).toString());
//        if (isError(json)) {
//            log.error("发送交易失败");
//            return null;
//        } else {
//            String result = json.getString("result");
//            log.info("发送成功 hash:{}", result);
//            return result;
//        }
//
//    }
//
//    /**
//     * 获取矿工费用
//     *
//     * @param utxos
//     * @return
//     */
//    public Long getOmniFee(List<UTXO> utxos) {
//        Long miniBtc = 546L;
//        Long feeRate = getFeeRate();
//        Long utxoAmount = 0L;
//        Long fee = 0L;
//        Long utxoSize = 0L;
//        for (UTXO output : utxos) {
//            utxoSize++;
//            if (utxoAmount > (fee + miniBtc)) {
//                break;
//            } else {
//                utxoAmount += output.getValue().value;
//                fee = (utxoSize * 148 + 34 * 2 + 10) * feeRate;
//            }
//        }
//        return fee;
//    }
//
//
//    public List<UTXO> getUnspents(String... address) throws Throwable {
//        List<UTXO> utxos = Lists.newArrayList();
//
//        try {
//            JSONArray outputs = JSONArray.parseArray(doRequest(METHOD_GET_LISTUNSPENT, 0, 99999999, address).toString());
//            if (outputs == null || outputs.size() == 0) {
//                System.out.println("交易异常，余额不足");
//            }
//            for (int i = 0; i < outputs.size(); i++) {
//                JSONObject outputsMap = outputs.getJSONObject(i);
//                String txid = outputsMap.get("txid").toString();
//                String vout = outputsMap.get("vout").toString();
//                String addr = outputsMap.get("address").toString();
//                String script = outputsMap.get("scriptPubKey").toString();
//                String amount = outputsMap.get("amount").toString();
//                BigDecimal bigDecimal = new BigDecimal(amount);
//                bigDecimal = bigDecimal.multiply(new BigDecimal(100000000));
//                // String confirmations = outputsMap.get("confirmations").toString();
//                UTXO utxo = new UTXO(Sha256Hash.wrap(txid), Long.valueOf(vout), Coin.valueOf(bigDecimal.longValue()),
//                        0, false, new Script(Hex.decode(script)), addr);
//                System.out.println(utxo.getAddress());
//                utxos.add(utxo);
//            }
//            return utxos;
//        } catch (Exception e) {
//            log.error("【BTC获取未消费列表】失败，", e);
//            return null;
//        }
//
//    }
//
//    /**
//     * 获取btc费率
//     *
//     * @return
//     */
//    public Long getFeeRate() {
//        try {
//            String httpGet1 = HttpUtils.doGet("https://bitcoinfees.earn.com/api/v1/fees/recommended").toString();
//            Map map = JSON.parseObject(httpGet1, Map.class);
//            Long fastestFee = Long.valueOf(map.get("fastestFee").toString());
//            return fastestFee;
//        } catch (Exception e) {
//            e.printStackTrace();
//            return 0L;
//        }
//    }

    /**
     * 验证地址的有效性
     *
     * @param address
     * @return
     * @throws Exception
     */
    public Object vailedAddress(String address) throws Throwable {
        return doRequest("validateaddress", address);
    }


    /**
     * 区块高度
     *
     * @return
     */
    public Object getBlockCount() throws Throwable {
        return doRequest(METHOD_GET_BLOCK_COUNT);

    }

    public boolean parseBlock(long index, List<CctFinanceLog> cctFinanceLogList, Map<String, String> addressMap, Boolean ifAudit) throws Throwable {
        String hash = doRequest(METHOD_GET_BLOCK_HASH, index).toString();
        String block = JSONObject.toJSONString(doRequest(METHOD_GET_BLOCK, hash));
        System.out.println("block：" + block);
        JSONObject jsonBlock = JSONObject.parseObject(block);
        int confirm = jsonBlock.getInteger("confirmations");
        if (confirm >= MIN_CONFIRMATION) {
            JSONArray jsonArrayTx = jsonBlock.getJSONArray("tx");
            if (jsonArrayTx == null || jsonArrayTx.size() == 0) {
                //没有交易
                return true;
            }
            Iterator<Object> iteratorTxs = jsonArrayTx.iterator();
            while (iteratorTxs.hasNext()) {
                String txid = (String) iteratorTxs.next();
                parseTx(txid, confirm, cctFinanceLogList, addressMap, ifAudit);
            }
            return true;
        } else {
            return false;
        }
    }

    public void parseTx(String txid, int coinfirm, List<CctFinanceLog> cctFinanceLogList, Map<String, String> addressMap, Boolean ifAudit) throws Throwable {
        String txStr = "";
        try {
            txStr = JSONObject.toJSONString(doRequest(METHOD_GET_TRANSACTION, txid));
        } catch (Exception e) {
            log.error(e.getMessage());
            return;
        }
        JSONObject jsonTransaction = JSONObject.parseObject(txStr);
        JSONArray jsonArrayVout = jsonTransaction.getJSONArray("details");
        String txId = jsonTransaction.getString("txId");
        if (jsonArrayVout == null || jsonArrayVout.size() == 0) {
            return;
        }
        Iterator<Object> iteratorVout = jsonArrayVout.iterator();
        while (iteratorVout.hasNext()) {
            JSONObject jsonVout = (JSONObject) iteratorVout.next();
            BigDecimal amount = jsonVout.getBigDecimal("amount");
            String category = jsonVout.getString("category");
            if (amount.compareTo(BigDecimal.ZERO) > 0 && "receive".equals(category)) {
                String address = jsonVout.getString("address");

                String userId = addressMap.get(address);
                if (StringUtils.isNotEmpty(userId)) {
                    //添加充值记录
                    log.info("用户充值");
                    CctFinanceLog cctFinanceLog = new CctFinanceLog();
                    cctFinanceLog.setTransactionHash(txId);
                    cctFinanceLog.setUserId(userId);
                    cctFinanceLog.setRecordType(USERASSETS_RECHARGE);
                    cctFinanceLog.setCoinId(""); //TODO coin id
                    cctFinanceLog.setStatus(judgeRechargeStatus(ifAudit));
                    cctFinanceLog.setRecordAmount(amount);
                    String detailStr = "转出地址：" + "" + "，转入地址：" + address;
                    cctFinanceLog.setRemark(detailStr);
                    cctFinanceLog.setFromAddress("");
                    cctFinanceLog.setToAddress(address);
                    cctFinanceLog.setIfMergeAssets(false);
//                        cctFinanceLog.setIfConfirm(confirmFlg);

                    cctFinanceLogList.add(cctFinanceLog);
                }
            }
        }
    }

    public void syncBtcBlock() throws Throwable {
        BigInteger freshBlockNumber = new BigInteger(getBlockCount().toString());

        MainNetInfo mainNetInfo = selectOneByWhereString(MainNetInfo.Net_name + "=", WalletConfig.BTC, MainNetInfo.class);
        if (mainNetInfo == null) {
            return;
        }
        String mainNetInfoId = mainNetInfo.getId();
        List<MainNetUserAddr> mainNetUserAddrList = selectListByWhereString(MainNetUserAddr.Main_net_id + "=", mainNetInfoId, MainNetUserAddr.class);
        Map<String, String> addressMap = new HashMap<>(mainNetUserAddrList.size());
        for (MainNetUserAddr mud : mainNetUserAddrList) {
            addressMap.put(mud.getMainNetAddr().toLowerCase(), mud.getUserId());
        }


        BigInteger currencyBlockNumber = BigInteger.valueOf(mainNetInfo.getBlockNumber());
        BigInteger blockSyncLimitNumber = BigInteger.valueOf(mainNetInfo.getBlockSyncLimitNumber());
        BigInteger blockDiffAmount = freshBlockNumber.subtract(currencyBlockNumber.add(BigInteger.valueOf(1)));
        if (blockSyncLimitNumber.compareTo(blockDiffAmount) > 0) {
            log.info("BTC主网当前高度：{}，系统同步高度：{}，区块高度差：{}，不足区块同步最低高度：{}，暂不进行区块同步",
                    freshBlockNumber, currencyBlockNumber, blockDiffAmount, blockDiffAmount);
            return;
        }

        List<CctFinanceLog> cctFinanceLogList = new ArrayList<>();

        BigInteger maxSyncBlockNum = new BigInteger("10");
        if (freshBlockNumber.subtract(currencyBlockNumber).compareTo(maxSyncBlockNum) > 0) {
            freshBlockNumber = currencyBlockNumber.add(maxSyncBlockNum);
        }

        Long confirmBlock = freshBlockNumber.longValue() - 3;
        System.out.println("currencyBlockNumber：" + currencyBlockNumber.longValue() + 1 + "，confirmBlock：" + confirmBlock);
        for (long i = currencyBlockNumber.longValue() + 1; i <= confirmBlock; i++) {
            parseBlock(i, cctFinanceLogList, addressMap, mainNetInfo.getIfAudit());
        }


        transactionTemplate.execute(new TransactionCallbackWithoutResult() {
            @Override
            protected void doInTransactionWithoutResult(TransactionStatus transactionStatus) {
                String errorMsg = "区块同步失败";
                if (cctFinanceLogList.size() > 0) {
                    for (CctFinanceLog uac : cctFinanceLogList) {
                        String userId = uac.getUserId();
                        BigDecimal amount = uac.getRecordAmount();
                        String coinId = uac.getCoinId();

                        ValueCheckUtils.notZero(insertIfNotNull(uac), errorMsg);

                        ValueCheckUtils.notZero(cctAssetsMapper.increase(userId, amount, coinId), errorMsg);
                    }
                }
                mainNetInfo.setBlockNumber(confirmBlock);
                ValueCheckUtils.notZero(update(mainNetInfo), errorMsg);

            }
        });

    }


    private Integer judgeRechargeStatus(Boolean ifAudit) {
        if (ifAudit) {
            return USERASSETS_RECHARGE_AUDIT_NONE;
        } else {
            return USERASSETS_RECHARGE_AUDIT_PASS;
        }
    }

    public Object doRequest(String method, Object... params) throws Throwable {
        WalletConfig walletConfig = WalletConfigInit.map.get(WalletConfig.BTC);
        String user = walletConfig.getUser();
        String password = walletConfig.getPassword();
        String host = walletConfig.getHost();
        String port = walletConfig.getPort();

        JsonRpcHttpClient client = null;
        try {
            String cred = org.apache.commons.codec.binary.Base64.encodeBase64String((user + ":" + password).getBytes());
            Map<String, String> headers = new HashMap<>(1);
            headers.put("Authorization", "Basic " + cred);
            client = new JsonRpcHttpClient(new URL("http://" + host + ":" + port), headers);
        } catch (Exception e) {
            e.printStackTrace();
            log.info("===com.bscoin.bit.env.CoinRpcClient:{} btc client !===", e.getMessage(), e);
        }
        Object str = client.invoke(method, params, Object.class);
        return str;
    }


    public static void main(String[] args) {

        String str = JSONObject.toJSONString("123");
        System.out.println(str);
    }

}
