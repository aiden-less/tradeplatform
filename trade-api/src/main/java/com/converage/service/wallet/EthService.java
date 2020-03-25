package com.converage.service.wallet;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.converage.architecture.dto.TasteFilePathConfig;
import com.converage.architecture.exception.BusinessException;
import com.converage.architecture.service.BaseService;
import com.converage.constance.WalletConst;
import com.converage.entity.assets.UserAssetsCharge;
import com.converage.entity.assets.WalletTransferRecord;
import com.converage.entity.chain.MainNetInfo;
import com.converage.entity.chain.MainNetUserAddr;
import com.converage.entity.market.TradeCoin;
import com.converage.entity.user.AssetsTurnoverExtralParam;
import com.converage.service.common.GlobalConfigService;
import com.converage.service.user.AssetsTurnoverService;
import com.converage.service.user.UserAssetsService;
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

import static com.converage.constance.AssetTurnoverConst.COMPANY_ID;
import static com.converage.constance.AssetTurnoverConst.TURNOVER_TYPE_RECHARGE;
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
    private UserAssetsService userAssetsService;

    @Autowired
    private TransactionTemplate transactionTemplate;

    @Autowired
    private EnvironmentUtils environmentUtils;

    @Autowired
    private EthService ethService;

    private static Web3j web3j;

    static {
        web3j = Web3j.build(new HttpService(WalletConst.ETH_MAIN_NET));
    }


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

        RawTransaction rawTransaction = RawTransaction.createTransaction(nonce, Convert.toWei("25", Convert.Unit.GWEI).toBigInteger(),
                Convert.toWei("60000", Convert.Unit.WEI).toBigInteger(), contractAddress, encodedFunction);

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


    public void syncEthBlock() throws IOException {
        System.out.println("syncEthBlock" + new Date());
        environmentUtils.checkIfPro();

        List<TradeCoin> wallets = selectListByWhereString(TradeCoin.If_Contract_token + "=", Boolean.TRUE, TradeCoin.class);

        Request<?, EthBlockNumber> ethBlockNumberRequest = web3j.ethBlockNumber();
        EthBlockNumber ethBlockNumber = ethBlockNumberRequest.send();
        BigInteger freshBlockNumber = ethBlockNumber.getBlockNumber();

        MainNetInfo mainNetInfo = selectOneByWhereString(MainNetInfo.Net_name + "=", WalletConst.ETH, MainNetInfo.class);
        if (mainNetInfo == null) {
            return;
        }
        String mainNetInfoId = mainNetInfo.getId();

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

        List<UserAssetsCharge> userAssetsChargeList = new ArrayList<>();

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
                TradeCoin filterSW = null;
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

//                if (settlementId == null) {
//                    continue;
//                }

//                Integer finalSettlementId = settlementId;
//                TradeCoin filterSW = wallets.stream().filter(sw -> sw.getSettlementId().equals(finalSettlementId)).findFirst().orElse(null);
//                if (filterSW == null) {
//                    log.error("error settlementId：{}", finalSettlementId);
//                    continue;
//                }

                UserAssetsCharge userAssetsCharge = new UserAssetsCharge();
                userAssetsCharge.setTransactionHash(transactionHash);
                userAssetsCharge.setUserId(userId);
                userAssetsCharge.setRecordType(USERASSETS_RECHARGE);
                userAssetsCharge.setSettlementId(0);
                userAssetsCharge.setStatus(judgeRechargeStatus(transactionFlag, filterSW.getIfRecharge()));
                userAssetsCharge.setRecordAmount(transferAmount);
                String detailStr = "转出地址：" + fromAddress + "，转入地址：" + toAddress;
                userAssetsCharge.setRemark(detailStr);
                userAssetsCharge.setFromAddress(fromAddress);
                userAssetsCharge.setToAddress(toAddress);
                userAssetsCharge.setIfDistributeScan(false);
                userAssetsCharge.setIfMergeAssets(false);
                userAssetsCharge.setIfConfirm(confirmFlg);

                userAssetsChargeList.add(userAssetsCharge);
            }
        }

        transactionTemplate.execute(new TransactionCallbackWithoutResult() {
            @Override
            protected void doInTransactionWithoutResult(TransactionStatus transactionStatus) {
                String errorMsg = "区块同步失败";
                if (userAssetsChargeList.size() > 0) {
                    for (UserAssetsCharge uac : userAssetsChargeList) {
                        ValueCheckUtils.notZero(insertIfNotNull(uac), errorMsg);
                    }
                }
                mainNetInfo.setBlockNumber(confirmBlock);
                ValueCheckUtils.notZero(update(mainNetInfo), errorMsg);

                for (UserAssetsCharge userAssetsCharge : userAssetsChargeList) {
                    String chargeId = userAssetsCharge.getId();
                    Integer status = userAssetsCharge.getStatus();
                    String userId = userAssetsCharge.getUserId();
                    Integer settlementId = userAssetsCharge.getSettlementId();
                    BigDecimal amount = userAssetsCharge.getRecordAmount();
                    String fromAddress = userAssetsCharge.getFromAddress();
                    String toAddress = userAssetsCharge.getToAddress();
                    String detailStr = "转出地址：" + fromAddress + "，转入地址：" + toAddress;


                    String remark;
                    if (status == USERASSETS_RECHARGE_AUDIT_PASS) {
                        ValueCheckUtils.notZero(userAssetsService.increaseUserAssets(userId, amount, settlementId), errorMsg);
                        remark = "通过";
                    } else if (status == USERASSETS_RECHARGE_AUDIT_NONE) {
                        remark = "审核中";
                    } else {
                        remark = "不通过";
                    }

                    AssetsTurnoverExtralParam extralParam = new AssetsTurnoverExtralParam();
                    extralParam.setChargeId(chargeId);

                    assetsTurnoverService.createAssetsTurnover(
                            userId, TURNOVER_TYPE_RECHARGE, amount, COMPANY_ID, userId, remark, settlementId,
                            detailStr, extralParam
                    );
                }

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
                UserAssetsCharge.If_distribute_scan + "=", Boolean.FALSE,
                UserAssetsCharge.Record_type + "=", USERASSETS_RECHARGE,
                UserAssetsCharge.Status + "=", USERASSETS_RECHARGE_AUDIT_PASS
        );
        List<UserAssetsCharge> userAssetsChargeList = selectListByWhereMap(whereMap, UserAssetsCharge.class);
        for (UserAssetsCharge uac : userAssetsChargeList) {
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
                UserAssetsCharge.If_merge_assets + "=", Boolean.FALSE,
                UserAssetsCharge.Record_type + "=", USERASSETS_RECHARGE,
                UserAssetsCharge.Status + "=", USERASSETS_RECHARGE_AUDIT_PASS
        );

        List<TradeCoin> tradeCoins = selectListByWhereString(TradeCoin.If_recharge + "=", Boolean.TRUE, TradeCoin.class);
        List<UserAssetsCharge> userAssetsChargeList = selectListByWhereMap(whereMap, UserAssetsCharge.class);

        for (UserAssetsCharge uac : userAssetsChargeList) {
            String userId = uac.getUserId();
            String id = uac.getId();
            String fromAddress = uac.getToAddress();
            Integer settlementId = uac.getSettlementId();
            String settlementName = userAssetsService.getSettlementNameById(settlementId);

            TradeCoin filterSW = tradeCoins.stream().filter(sw -> sw.getSettlementId().equals(settlementId)).findFirst().orElse(null);
            if (filterSW == null) {
                continue;
            }

            BigDecimal minMergeAmount = filterSW.getMinMergeAmount();
            BigDecimal recordAmount = uac.getRecordAmount();

            if (minMergeAmount.compareTo(recordAmount) > 0) {
                continue;
            }

            String walletName = WalletConst.ETH;
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
                EthSendTransaction ethSendTransaction = ethService.sendEthTokenTransaction(fromAddress, mergeRechargeAddr, contractAddress, recordAmount, userPrivateKey);
                if (!ethSendTransaction.hasError()) {
                    String transactionHash = ethSendTransaction.getTransactionHash();
                    WalletTransferRecord wtr = new WalletTransferRecord(
                            settlementName, "充值资产归集", fromAddress, mergeRechargeAddr, recordAmount, new Timestamp(System.currentTimeMillis()), transactionHash
                    );
                    ethService.insertIfNotNull(wtr);
                }
            } catch (ExecutionException | InterruptedException | IOException e) {
                e.printStackTrace();
            }


        }
    }


    //归集提现手续费
    public void mergeWithdraw() {
        environmentUtils.checkIfPro();

        String errorMsg = "归集提现手续费失败";
        String Merge_Transfer_Free_Address = globalConfigService.get(GlobalConfigService.Enum.Merge_Transfer_Free_Address);
        Map<String, Object> whereMap = ImmutableMap.of(
                UserAssetsCharge.If_merge_poundage + "=", Boolean.FALSE,
                UserAssetsCharge.Record_type + "=", USERASSETS_WITHDRAW,
                UserAssetsCharge.Status + "=", USERASSETS_RECHARGE_AUDIT_PASS
        );

        List<TradeCoin> tradeCoins = selectListByWhereString(TradeCoin.If_recharge + "=", Boolean.TRUE, TradeCoin.class);
        List<UserAssetsCharge> userAssetsChargeList = selectListByWhereMap(whereMap, UserAssetsCharge.class);


        for (UserAssetsCharge uac : userAssetsChargeList) {
            String userId = uac.getUserId();
            String id = uac.getId();
            String fromAddress = uac.getFromAddress();
//            String toAddress = uac.getToAddress();

            BigDecimal poundageAmount = uac.getPoundageAmount();
            Integer settlementId = uac.getSettlementId();
            String settlementName = userAssetsService.getSettlementNameById(settlementId);

            TradeCoin filterSW = tradeCoins.stream().filter(sw -> sw.getSettlementId().equals(settlementId)).findFirst().orElse(null);
            if (filterSW == null) {
                continue;
            }

            BigDecimal minMergeAmount = filterSW.getMinMergeAmount();
            if (minMergeAmount.compareTo(poundageAmount) > 0) {
                continue;
            }

            String walletName = WalletConst.ETH;
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
            try {
                EthSendTransaction ethSendTransaction = sendEthTransaction(fromAddress, Merge_Transfer_Free_Address, poundageAmount, userPrivateKey);
                if (!ethSendTransaction.hasError()) {
                    String transactionHash = ethSendTransaction.getTransactionHash();
                    WalletTransferRecord wtr = new WalletTransferRecord(
                            settlementName, "提现手续费归集", fromAddress, Merge_Transfer_Free_Address, poundageAmount, new Timestamp(System.currentTimeMillis()), transactionHash
                    );
                    insertIfNotNull(wtr);
                }
            } catch (ExecutionException | InterruptedException | IOException e) {
                e.printStackTrace();
            }


        }


    }


    public BigDecimal getERC20TokenBalance(String address, String contractAddress) throws IOException {
        ValueCheckUtils.notEmptyString(address, "钱包地址异常");
        ValueCheckUtils.notEmptyString(contractAddress, "合约地址异常");
        String DATA_PREFIX = "0x70a08231000000000000000000000000";
        String value = Admin.build(new HttpService(WalletConst.ETH_MAIN_NET))
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


    public static void main(String[] args) throws IOException {
        String walletKey = "ts-yek-tellaw";

        String keyStore = FileUtils.readFileToString(new File("C:\\Users\\Administrator\\Desktop\\keystore_f71cecf62f794fa3be9d46092bbfc5fc.json"), "UTF-8");
        Credentials credentials = ETHWalletUtils.loadCredentialsByKeystore(keyStore, walletKey + "f71cecf62f794fa3be9d46092bbfc5fc");

        String address = credentials.getAddress();
        String privateKey = credentials.getEcKeyPair().getPrivateKey().toString(16);

        System.out.println(address);
        System.out.println(privateKey);


    }
}
