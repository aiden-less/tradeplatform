package com.converage.service.wallet;

import com.converage.entity.assets.CctFinanceLog;
import com.converage.entity.chain.WalletConfig;
import com.converage.entity.wallet.WalletAccount;
import com.converage.mapper.user.CctAssetsMapper;
import com.google.common.collect.ImmutableMap;
import com.converage.architecture.dto.TasteFilePathConfig;
import com.converage.architecture.exception.BusinessException;
import com.converage.architecture.service.BaseService;
import com.converage.entity.assets.WalletTransferRecord;
import com.converage.entity.chain.MainNetInfo;
import com.converage.entity.chain.MainNetUserAddr;
import com.converage.entity.market.TradeCoin;
import com.converage.service.common.GlobalConfigService;
import com.converage.service.user.AssetsTurnoverService;
import com.converage.utils.BigDecimalUtils;
import com.converage.utils.EnvironmentUtils;
import com.converage.utils.ValueCheckUtils;
import com.converage.utils.eth.ETHWalletUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallbackWithoutResult;
import org.springframework.transaction.support.TransactionTemplate;
import org.web3j.abi.FunctionEncoder;
import org.web3j.abi.FunctionReturnDecoder;
import org.web3j.abi.TypeReference;
import org.web3j.abi.datatypes.Address;
import org.web3j.abi.datatypes.Function;
import org.web3j.abi.datatypes.Type;
import org.web3j.abi.datatypes.generated.Uint256;
import org.web3j.crypto.Credentials;
import org.web3j.crypto.RawTransaction;
import org.web3j.crypto.TransactionEncoder;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.admin.Admin;
import org.web3j.protocol.core.DefaultBlockParameter;
import org.web3j.protocol.core.DefaultBlockParameterName;
import org.web3j.protocol.core.DefaultBlockParameterNumber;
import org.web3j.protocol.core.Request;
import org.web3j.protocol.core.methods.response.*;
import org.web3j.protocol.http.HttpService;
import org.web3j.utils.Convert;
import org.web3j.utils.Numeric;

import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.Timestamp;
import java.util.*;
import java.util.concurrent.ExecutionException;

import static com.converage.constance.SettlementConst.*;

@Service
@Slf4j
public class EthService extends BaseService {

    private static final Integer SCALE_LENGTH = 6;

    @Autowired
    private GlobalConfigService globalConfigService;

    @Autowired
    private AssetsTurnoverService assetsTurnoverService;

    @Autowired
    private CctAssetsMapper cctAssetsMapper;

    @Autowired
    private TransactionTemplate transactionTemplate;

    @Autowired
    private EnvironmentUtils environmentUtils;

    private static Web3j web3j = Web3j.build(new HttpService("https://mainnet.infura.io/v3/eac43464a30d4fcc890f3656e8290e45"));

    public WalletAccount createAccount(String walletName, String fileName, String pwd) {
        return ETHWalletUtils.generateMnemonic(walletName, fileName, pwd);
    }

    //以太坊转账
    public EthSendTransaction sendEthTransaction(String fromAddress, String toAddress, BigDecimal amount, String privateKey) throws ExecutionException, InterruptedException, IOException {
        Credentials credentials = Credentials.create(privateKey);

        amount = Convert.toWei(amount, Convert.Unit.ETHER);

        EthGetTransactionCount ethGetTransactionCount = web3j.ethGetTransactionCount(fromAddress, DefaultBlockParameterName.LATEST).send();
        BigInteger nonce = ethGetTransactionCount.getTransactionCount();

        RawTransaction rawTransaction = RawTransaction.createEtherTransaction(
                nonce, Convert.toWei("18", Convert.Unit.GWEI).toBigInteger(),
                Convert.toWei("45000", Convert.Unit.WEI).toBigInteger(),
                toAddress,
                amount.toBigInteger()
        );
        byte[] signedMessage = TransactionEncoder.signMessage(rawTransaction, credentials);
        String hexValue = Numeric.toHexString(signedMessage);

        EthSendTransaction ethSendTransaction = web3j.ethSendRawTransaction(hexValue).sendAsync().get();
        if (ethSendTransaction.hasError()) {
            log.info("transfer error:", ethSendTransaction.getError().getMessage());
            throw new BusinessException("转账失败");
        } else {
            String transactionHash = ethSendTransaction.getTransactionHash();
            log.info("Transfer transactionHash:" + transactionHash);
        }
        return ethSendTransaction;
    }

    //以太坊代币转账
    public EthSendTransaction sendEthTokenTransaction(String fromAddress, String toAddress, String contractAddress, BigDecimal amount, String privateKey) throws ExecutionException, InterruptedException, IOException {
        Credentials credentials = Credentials.create(privateKey);

        EthGetTransactionCount ethGetTransactionCount = web3j.ethGetTransactionCount(
                fromAddress, DefaultBlockParameterName.LATEST).sendAsync().get();

        BigInteger nonce = ethGetTransactionCount.getTransactionCount();


        TradeCoin tradeCoin = selectOneByWhereString(TradeCoin.Contract_addr + "=", contractAddress, TradeCoin.class);
        Integer decimalPoint = tradeCoin.getDecimalPoint();

        amount = amount.setScale(decimalPoint, BigDecimal.ROUND_DOWN);

        amount = amount.multiply(BigDecimal.valueOf(10).pow(decimalPoint)).stripTrailingZeros();

        BigInteger bigInteger = amount.toBigInteger();

        Function function = new Function(
                "transfer",
                Arrays.asList(new Address(toAddress), new Uint256(bigInteger)),
                Collections.singletonList(new TypeReference<Type>() {
                }));

        String encodedFunction = FunctionEncoder.encode(function);

        RawTransaction rawTransaction = RawTransaction.createTransaction(nonce, Convert.toWei("18", Convert.Unit.GWEI).toBigInteger(),
                Convert.toWei("45000", Convert.Unit.WEI).toBigInteger(), contractAddress, encodedFunction);

        byte[] signedMessage = TransactionEncoder.signMessage(rawTransaction, credentials);
        String hexValue = Numeric.toHexString(signedMessage);

        log.debug("transfer hexValue:" + hexValue);

        EthSendTransaction ethSendTransaction = web3j.ethSendRawTransaction(hexValue).send();
        if (ethSendTransaction.hasError()) {
            log.info("transfer error:", ethSendTransaction.getError().getMessage());
        } else {
            String transactionHash = ethSendTransaction.getTransactionHash();
            log.info("Transfer transactionHash:" + transactionHash);
        }
        return ethSendTransaction;
    }

    public EthBlock getBlockEthBlock(Integer blockNumber) throws IOException {
        Request<?, EthBlockNumber> ethBlockNumberRequest = web3j.ethBlockNumber();
        EthBlockNumber ethBlockNumber = ethBlockNumberRequest.send();
        System.out.println(ethBlockNumber.getBlockNumber());

        DefaultBlockParameter defaultBlockParameter = new DefaultBlockParameterNumber(blockNumber);
        Request<?, EthBlock> request = web3j.ethGetBlockByNumber(defaultBlockParameter, true);
        return request.send();
    }

    //同步区块
    public void syncEthBlock() throws IOException {
        System.out.println("syncEthBlock" + new Date());
        environmentUtils.checkIfPro();


        Request<?, EthBlockNumber> ethBlockNumberRequest = web3j.ethBlockNumber();
        EthBlockNumber ethBlockNumber = ethBlockNumberRequest.send();
        BigInteger freshBlockNumber = ethBlockNumber.getBlockNumber();

        MainNetInfo mainNetInfo = selectOneByWhereString(MainNetInfo.Net_name + "=", WalletConfig.ETH, MainNetInfo.class);
        if (mainNetInfo == null) {
            return;
        }
        String mainNetInfoId = mainNetInfo.getId();

        List<TradeCoin> wallets = selectListByWhereString(TradeCoin.Main_net_ids + "LIKE ", "%" + mainNetInfoId + "%", TradeCoin.class);


        BigInteger currencyBlockNumber = BigInteger.valueOf(mainNetInfo.getBlockNumber());
        BigInteger blockSyncLimitNumber = BigInteger.valueOf(mainNetInfo.getBlockSyncLimitNumber());
        BigInteger blockDiffAmount = freshBlockNumber.subtract(currencyBlockNumber.add(BigInteger.valueOf(1)));
        if (blockSyncLimitNumber.compareTo(blockDiffAmount) > 0) {
            log.info("ETH主网当前高度：{}，系统同步高度：{}，区块高度差：{}，不足区块同步最低高度：{}，暂不进行区块同步",
                    freshBlockNumber, currencyBlockNumber, blockDiffAmount, blockDiffAmount);
            return;
        }

        List<MainNetUserAddr> mainNetUserAddrList = selectListByWhereString(MainNetUserAddr.Main_net_id + "=", mainNetInfoId, MainNetUserAddr.class);
        Map<String, String> addressMap = new HashMap<>(mainNetUserAddrList.size());
        for (MainNetUserAddr mud : mainNetUserAddrList) {
            addressMap.put(mud.getMainNetAddr().toLowerCase(), mud.getUserId());
        }

        List<CctFinanceLog> cctFinanceLogList = new ArrayList<>();

//        BigInteger maxSyncBlockNum = new BigInteger("20");
//        if (freshBlockNumber.subtract(currencyBlockNumber).compareTo(maxSyncBlockNum) > 0) {
//            freshBlockNumber = currencyBlockNumber.add(maxSyncBlockNum);
//        }

        Long confirmBlock = freshBlockNumber.longValue() - 3;
        System.out.println("currencyBlockNumber：" + currencyBlockNumber.longValue() + 1 + "，confirmBlock：" + confirmBlock);
        for (long i = currencyBlockNumber.longValue() + 1; i <= confirmBlock; i++) {
            System.out.println(i);
            DefaultBlockParameter defaultBlockParameter = new DefaultBlockParameterNumber(i);
            Request<?, EthBlock> ethBlockRequest = web3j.ethGetBlockByNumber(defaultBlockParameter, true);
            EthBlock ethBlock = ethBlockRequest.send();
            List<EthBlock.TransactionResult> transactionResults = ethBlock.getBlock().getTransactions();
            for (EthBlock.TransactionResult transactionResult : transactionResults) {
                EthBlock.TransactionObject transactionObject = (EthBlock.TransactionObject) transactionResult;
                String transactionHash = transactionObject.getHash();
                String input = transactionObject.getInput();
                BigInteger transferRecordValue = transactionObject.getValue();
                String fromAddress = transactionObject.getFrom();
                String toAddress = transactionObject.getTo();

                BigDecimal transferAmount = new BigDecimal(transferRecordValue.toString());
//                Integer settlementId = null;
                TradeCoin filterSW;
                if (transferAmount.compareTo(BigDecimal.ZERO) == 0) { //ERC TOKEN
                    String contractAddress = transactionObject.getTo();

                    //系统内支持的代币
                    filterSW = wallets.stream().filter(sw -> sw.getContractAddr().equalsIgnoreCase(contractAddress)).findFirst().orElse(null);
                    if (filterSW == null) {
                        continue;
                    }
//                    settlementId = filterSW.getSettlementId();
                    if (input != null && input.length() > 136) {
                        String tranMethodId = input.substring(0, 10);
                        List<Type> values = FunctionReturnDecoder.decode(input.substring(input.length() - 64, input.length()), org.web3j.abi.Utils.convert(Arrays.asList(new TypeReference<Uint256>() {
                        })));
                        if (!values.isEmpty()) {
                            String value = values.get(0).getValue().toString();
                            Integer bigDecimalPoint = filterSW.getDecimalPoint();
                            transferAmount = new BigDecimal(value).divide(BigDecimal.valueOf(10).pow(bigDecimalPoint));

                            BigDecimal rechargeMinAmount = filterSW.getMinRechargeAmount();
                            if (rechargeMinAmount.compareTo(transferAmount) > 0) {
                                log.info("交易哈希：{}，币种名称：{}，记录数目：{}，充值最小数目：{}", transactionHash, filterSW.getCoinName(), transferAmount, rechargeMinAmount);
                                continue;
                            }

                        } else {
                            continue;
                        }

                        String toAddressInput;
                        if ("0xa9059cbb".equals(tranMethodId)) { //普通转账
                            toAddressInput = input.substring(10, 74);
                        } else if ("0x23b872dd".equals(tranMethodId)) { //赋权转账
                            toAddressInput = input.substring(74, 138);
                        } else {
                            log.info("非转账交易 hash：{}", transactionHash);
                            continue;
                        }

                        List<Type> addressType = FunctionReturnDecoder.decode(toAddressInput, org.web3j.abi.Utils.convert(Arrays.asList(new TypeReference<Address>() {
                        })));
                        if (!addressType.isEmpty()) {
                            toAddress = addressType.get(0).getValue().toString();
                        }
                    }

                } else if (transferAmount.compareTo(BigDecimal.ZERO) > 0) { //ETH
                    filterSW = selectOneByWhereString(MainNetInfo.Net_name + "=", WalletConfig.ETH, TradeCoin.class);
                    transferAmount = fromWei(transferRecordValue);
                } else {
                    log.error("异常记录，转账金额：{}", transferAmount.toPlainString());
                    continue;
                }

                if (toAddress == null) {
                    continue;
                }
                String userId = addressMap.get(toAddress.toLowerCase());
                if (StringUtils.isEmpty(userId)) {
                    continue;
                }

                Boolean transactionFlag = false;
                Boolean confirmFlg = false;
                EthGetTransactionReceipt receipt = web3j.ethGetTransactionReceipt(transactionHash).send();
                if (receipt.getTransactionReceipt().isPresent()) {
                    TransactionReceipt re = receipt.getTransactionReceipt().get();
                    transactionFlag = judgeStatus(re.getStatus());
                    confirmFlg = true;
                }


                String coinId = filterSW.getId();

//                Integer finalSettlementId = settlementId;
//                TradeCoin filterSW = wallets.stream().filter(sw -> sw.getSettlementId().equals(finalSettlementId)).findFirst().orElse(null);
//                if (filterSW == null) {
//                    log.error("error settlementId：{}", finalSettlementId);
//                    continue;
//                }

                CctFinanceLog cctFinanceLog = new CctFinanceLog();
                cctFinanceLog.setTransactionHash(transactionHash);
                cctFinanceLog.setUserId(userId);
                cctFinanceLog.setRecordType(USERASSETS_RECHARGE);
                cctFinanceLog.setCoinId(coinId);
                cctFinanceLog.setStatus(judgeRechargeStatus(transactionFlag, filterSW.getIfRecharge()));
                cctFinanceLog.setRecordAmount(transferAmount);
                String detailStr = "转出地址：" + fromAddress + "，转入地址：" + toAddress;
                cctFinanceLog.setRemark(detailStr);
                cctFinanceLog.setFromAddress(fromAddress);
                cctFinanceLog.setToAddress(toAddress);
                cctFinanceLog.setIfMergeAssets(false);
                cctFinanceLog.setIfConfirm(confirmFlg);

                cctFinanceLogList.add(cctFinanceLog);
            }
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

    //ETHER -> WEI
    private BigInteger toWei(BigDecimal amount) {
        return new BigInteger(amount.multiply(BigDecimal.valueOf(Math.pow(10, 18))).toPlainString());
    }

    //WEI -> ETHER
    private static BigDecimal fromWei(BigInteger amount) {
        BigDecimal pow = BigDecimal.valueOf(Math.pow(10, 18));
        BigDecimal bigDecimal = new BigDecimal(amount.toString());
        return BigDecimalUtils.divide(bigDecimal, pow, SCALE_LENGTH);
    }

    private Boolean judgeStatus(String status) {
        if ("0x1".equals(status)) {
            return true;
        } else {
            return false;
        }
    }

    private Integer judgeRechargeStatus(Boolean flag, Boolean ifAudit) {
        if (flag) {
            if (ifAudit) {
                return USERASSETS_RECHARGE_AUDIT_NONE;
            } else {
                return USERASSETS_RECHARGE_AUDIT_PASS;
            }
        } else {
            return USERASSETS_RECHARGE_AUDIT_UNPASS;
        }
    }

    //分配转账手续费
    public void distributePoundage4EthTran() {
        environmentUtils.checkIfPro();

        String Distribute_Transfer_Free_Address = globalConfigService.get(GlobalConfigService.Enum.Distribute_Transfer_Free_Address);
        String privateKey = globalConfigService.get(GlobalConfigService.Enum.Distribute_Transfer_Free_Private_Key);
        BigDecimal decimal = new BigDecimal(globalConfigService.get(GlobalConfigService.Enum.Distribute_Transfer_Free_Amount));

        Map<String, Object> whereMap = ImmutableMap.of(
                CctFinanceLog.Record_type + "=", USERASSETS_RECHARGE,
                CctFinanceLog.Status + "=", USERASSETS_RECHARGE_AUDIT_PASS
        );
        List<CctFinanceLog> cctFinanceLogList = selectListByWhereMap(whereMap, CctFinanceLog.class);
        for (CctFinanceLog uac : cctFinanceLogList) {
            String id = uac.getId();
            String toAddress = uac.getToAddress();
            try {
                EthSendTransaction ethSendTransaction = sendEthTransaction(Distribute_Transfer_Free_Address, toAddress, decimal, privateKey);
                if (!ethSendTransaction.hasError()) {
                    String transactionHash = ethSendTransaction.getTransactionHash();
                    WalletTransferRecord wtr = new WalletTransferRecord(
                            "ETH", "分配手续费", Distribute_Transfer_Free_Address, toAddress, decimal, new Timestamp(System.currentTimeMillis()), transactionHash
                    );
                    insertIfNotNull(wtr);
                }
            } catch (ExecutionException | InterruptedException | IOException e) {
                e.printStackTrace();
            }
        }
    }

    //归集充值资产
    public void mergeRecharge() {
        environmentUtils.checkIfPro();

        String errorMsg = "归集充值资产失败";
        String mergeRechargeAddr = globalConfigService.get(GlobalConfigService.Enum.Merge_Transfer_Free_Address);
        Map<String, Object> whereMap = ImmutableMap.of(
                CctFinanceLog.If_merge_assets + "=", Boolean.FALSE,
                CctFinanceLog.Record_type + "=", USERASSETS_RECHARGE,
                CctFinanceLog.Status + "=", USERASSETS_RECHARGE_AUDIT_PASS
        );

        List<TradeCoin> tradeCoins = selectListByWhereString(TradeCoin.If_recharge + "=", Boolean.TRUE, TradeCoin.class);
        List<CctFinanceLog> cctFinanceLogList = selectListByWhereMap(whereMap, CctFinanceLog.class);

        for (CctFinanceLog uac : cctFinanceLogList) {
            String userId = uac.getUserId();
            String id = uac.getId();
            String fromAddress = uac.getToAddress();
            String coinId = uac.getCoinId();
            TradeCoin filterSW = tradeCoins.stream().filter(sw -> sw.getId().equals(coinId)).findFirst().orElse(null);
            if (filterSW == null) {
                continue;
            }

            String settlementName = filterSW.getCoinName();

            BigDecimal minMergeAmount = filterSW.getMinMergeAmount();
            BigDecimal recordAmount = uac.getRecordAmount();

            if (minMergeAmount.compareTo(recordAmount) > 0) {
                continue;
            }

            String walletName = WalletConfig.ETH;
            String filePath = TasteFilePathConfig.walletEthFolder + "/" + walletName + "/" + "keystore_" + userId + ".json";
            String keyStore = null;
            try {
                keyStore = FileUtils.readFileToString(new File(filePath), "UTF-8");
            } catch (IOException e) {
                e.printStackTrace();
            }
            String walletKey = globalConfigService.get(GlobalConfigService.Enum.WALLET_KEY);
            Credentials credentials = ETHWalletUtils.loadCredentialsByKeystore(keyStore, walletKey + userId);
            ValueCheckUtils.notEmpty(credentials, errorMsg);
            String userPrivateKey = credentials.getEcKeyPair().getPrivateKey().toString(16);
            String contractAddress = filterSW.getContractAddr();
            try {
                EthSendTransaction ethSendTransaction = sendEthTokenTransaction(fromAddress, mergeRechargeAddr, contractAddress, recordAmount, userPrivateKey);
                if (!ethSendTransaction.hasError()) {
                    String transactionHash = ethSendTransaction.getTransactionHash();
                    WalletTransferRecord wtr = new WalletTransferRecord(
                            settlementName, "充值资产归集", fromAddress, mergeRechargeAddr, recordAmount, new Timestamp(System.currentTimeMillis()), transactionHash
                    );
                    insertIfNotNull(wtr);
                }
            } catch (ExecutionException | InterruptedException | IOException e) {
                e.printStackTrace();
            }


        }
    }


    //归集提现手续费
    public void mergeWithdraw() {
//        environmentUtils.checkIfPro();
//
//        String errorMsg = "归集提现手续费失败";
//        String Merge_Transfer_Free_Address = globalConfigService.get(GlobalConfigService.Enum.Merge_Transfer_Free_Address);
//        Map<String, Object> whereMap = ImmutableMap.of(
//                CctFinanceLog.If_merge_poundage + "=", Boolean.FALSE,
//                CctFinanceLog.Record_type + "=", USERASSETS_WITHDRAW,
//                CctFinanceLog.Status + "=", USERASSETS_RECHARGE_AUDIT_PASS
//        );
//
//        List<TradeCoin> tradeCoins = selectListByWhereString(TradeCoin.If_recharge + "=", Boolean.TRUE, TradeCoin.class);
//        List<CctFinanceLog> userAssetsChargeList = selectListByWhereMap(whereMap, CctFinanceLog.class);
//
//
//        for (CctFinanceLog uac : userAssetsChargeList) {
//            String userId = uac.getUserId();
//            String id = uac.getId();
//            String fromAddress = uac.getFromAddress();
////            String toAddress = uac.getToAddress();
//
//            BigDecimal poundageAmount = uac.getPoundageAmount();
//            Integer settlementId = uac.getSettlementId();
//            String settlementName = userAssetsService.getSettlementNameById(settlementId);
//
//            TradeCoin filterSW = tradeCoins.stream().filter(sw -> sw.getSettlementId().equals(settlementId)).findFirst().orElse(null);
//            if (filterSW == null) {
//                continue;
//            }
//
//            BigDecimal minMergeAmount = filterSW.getMinMergeAmount();
//            if (minMergeAmount.compareTo(poundageAmount) > 0) {
//                continue;
//            }
//
//            String walletName = WalletConfig.ETH;
//            String filePath = TasteFilePathConfig.walletEthFolder + "/" + walletName + "/" + "keystore_" + userId + ".json";
//
//            String keyStore = null;
//            try {
//                keyStore = FileUtils.readFileToString(new File(filePath), "UTF-8");
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//            String walletKey = globalConfigService.get(GlobalConfigService.Enum.WALLET_KEY);
//            Credentials credentials = ETHWalletUtils.loadCredentialsByKeystore(keyStore, walletKey + userId);
//            ValueCheckUtils.notEmpty(credentials, errorMsg);
//            String userPrivateKey = credentials.getEcKeyPair().getPrivateKey().toString(16);
//            try {
//                EthSendTransaction ethSendTransaction = sendEthTransaction(fromAddress, Merge_Transfer_Free_Address, poundageAmount, userPrivateKey);
//                if (!ethSendTransaction.hasError()) {
//                    String transactionHash = ethSendTransaction.getTransactionHash();
//                    WalletTransferRecord wtr = new WalletTransferRecord(
//                            settlementName, "提现手续费归集", fromAddress, Merge_Transfer_Free_Address, poundageAmount, new Timestamp(System.currentTimeMillis()), transactionHash
//                    );
//                    insertIfNotNull(wtr);
//                }
//            } catch (ExecutionException | InterruptedException | IOException e) {
//                e.printStackTrace();
//            }
//        }


    }


    public BigDecimal getERC20TokenBalance(String address, String contractAddress) throws IOException {
        ValueCheckUtils.notEmptyString(address, "钱包地址异常");
        ValueCheckUtils.notEmptyString(contractAddress, "合约地址异常");
        String DATA_PREFIX = "0x70a08231000000000000000000000000";
        String value = Admin.build(new HttpService(WalletConfig.ETH))
                .ethCall(org.web3j.protocol.core.methods.request.Transaction.createEthCallTransaction(address,
                        contractAddress, DATA_PREFIX + address.substring(2)), DefaultBlockParameterName.PENDING).send().getValue();
        String s = new BigInteger(value.substring(2), 16).toString();

        return new BigDecimal(s);
    }

    public BigDecimal getETHBalance(String address) throws IOException {
        ValueCheckUtils.notEmptyString(address, "钱包地址异常");

        BigInteger bigInteger = web3j.ethGetBalance(address, DefaultBlockParameterName.LATEST).send().getBalance();
        BigDecimal bigDecimal = new BigDecimal(bigInteger);
        return Convert.fromWei(bigDecimal, Convert.Unit.ETHER);
    }


    public static void main(String[] args) throws IOException, ExecutionException, InterruptedException {

        EthService ethService = new EthService();
        String hash = ethService.sendEthTokenTransaction(
                "0xd6204879eE209C3513243813004a5763Daf5c4d1",
                "0xB6Ae85cF6924a1436C89DBe1c91013C7e8dCeb70",
                "0x7508b4571892d4e04E9aacdf8cC3F2A66949a1F3",
                BigDecimal.valueOf(10000),
                "0f33deb9244e239531333643fe58fc77bb4663819546cbdc062767156eeb13bc").getTransactionHash();

        System.out.println(hash);

    }


}
