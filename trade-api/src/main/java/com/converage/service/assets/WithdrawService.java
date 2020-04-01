package com.converage.service.assets;

import com.converage.mapper.user.CctAssetsMapper;
import com.converage.service.wallet.EthService;
import com.google.common.collect.ImmutableMap;
import com.converage.architecture.exception.BusinessException;
import com.converage.architecture.service.BaseService;
import com.converage.constance.SettlementConst;
import com.converage.entity.assets.CctAssets;
import com.converage.entity.assets.UserAssetsCharge;
import com.converage.entity.assets.WalletTransferRecord;
import com.converage.entity.sys.Subscriber;
import com.converage.entity.market.TradeCoin;
import com.converage.entity.user.AssetsTurnoverExtralParam;
import com.converage.entity.user.Certification;
import com.converage.entity.user.User;
import com.converage.service.common.GlobalConfigService;
import com.converage.service.user.AssetsTurnoverService;
import com.converage.service.user.UserAssetsService;
import com.converage.utils.EncryptUtils;
import com.converage.utils.EnvironmentUtils;
import com.converage.utils.MineDateUtils;
import com.converage.utils.ValueCheckUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallbackWithoutResult;
import org.springframework.transaction.support.TransactionTemplate;
import org.web3j.protocol.core.methods.response.EthSendTransaction;

import java.io.IOException;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

import static com.converage.constance.AssetTurnoverConst.COMPANY_ID;
import static com.converage.constance.AssetTurnoverConst.TURNOVER_TYPE_WITHDRAW;
import static com.converage.constance.UserConst.USER_CERT_STATUS_PASS;

@Service
@Slf4j
public class WithdrawService extends BaseService {

    @Autowired
    private TransactionTemplate transactionTemplate;

    @Autowired
    private UserAssetsService userAssetsService;

    @Autowired
    private CctAssetsMapper cctAssetsMapper;

    @Autowired
    private AssetsTurnoverService assetsTurnoverService;

    @Autowired
    private GlobalConfigService globalConfigService;

    @Autowired
    private EthService ethService;

    @Autowired
    private EnvironmentUtils environmentUtils;

    /**
     * 申请提现
     *
     * @param userId
     * @param amount
     * @param coinId
     */
    public Map<String, Object> applyWithdraw(String userId, BigDecimal amount, String coinId, String payPassword, String toAddress) {
        User sourceUser = selectOneById(userId, User.class);
        if (!sourceUser.getIfCanWithdraw()) {
            throw new BusinessException("提现功能已被禁用");
        }
        String userPayPassword = sourceUser.getPayPassword();

        ValueCheckUtils.notEmpty(userPayPassword, "请先设置支付密码");
        ValueCheckUtils.notEmpty(payPassword, "支付密码不能为空");

        if (!EncryptUtils.md5Password(payPassword).equals(userPayPassword)) {
            throw new BusinessException("支付密码错误");
        }

        if (amount.compareTo(BigDecimal.ZERO) < 0) {
            throw new BusinessException("提现金额必须大于0");
        }

        TradeCoin tradeCoin = selectOneByWhereString(TradeCoin.Id + "=", coinId, TradeCoin.class);

        BigDecimal withdrawMin = tradeCoin.getMinWithDrawAmount();
        if (amount.compareTo(withdrawMin) < 0) {
            throw new BusinessException("提现最低金额为：" + withdrawMin.stripTrailingZeros().toPlainString());
        }

        BigDecimal withdrawMax = tradeCoin.getMaxWithDrawAmount();
        if (withdrawMax.compareTo(amount) < 0) {
            throw new BusinessException("提现最大额度为：" + withdrawMax.stripTrailingZeros().toPlainString());
        }

        Map<String, Object> whereMap2 = ImmutableMap.of(
                Certification.User_id + "= ", userId,
                Certification.Status + "=", USER_CERT_STATUS_PASS
        );
        List<Certification> certificationPo = selectListByWhereMap(whereMap2, Certification.class);
        ValueCheckUtils.notEmpty(certificationPo, "须通过实名认证后再提现");

        UserAssetsCharge userAssetsCharge = new UserAssetsCharge();
        userAssetsCharge.setUserId(userId);

        //扣除手续费
        BigDecimal withdrawRate = tradeCoin.getWithDrawPoundageRate().divide(BigDecimal.valueOf(100));
        BigDecimal poundage = amount.multiply(withdrawRate);
        userAssetsCharge.setRecordAmount(amount);
        userAssetsCharge.setPoundageAmount(poundage);
        userAssetsCharge.setRecordType(SettlementConst.USERASSETS_WITHDRAW);
        userAssetsCharge.setCoinId("");


        BigDecimal withdrawAuditLimitAmount = tradeCoin.getWithdrawAuditLimitAmount();

        String fromAddress = globalConfigService.get(GlobalConfigService.Enum.Withdraw_Output_Address);
        userAssetsCharge.setFromAddress(fromAddress);
        userAssetsCharge.setToAddress(toAddress);
        userAssetsCharge.setIfMergePoundage(false);
        userAssetsCharge.setRemark("提现金额：" + amount + " 提现地址：" + toAddress + "，手续费：" + poundage);

        CctAssets sourceCctAssets = selectOneByWhereMap(
                ImmutableMap.of(CctAssets.User_id + "=", userId, CctAssets.Coin_id + "=", coinId), CctAssets.class
        );
        BigDecimal decreaseAmount = amount.add(poundage);
        if (sourceCctAssets.getAssetsAmount().compareTo(decreaseAmount) < 0) {
            throw new BusinessException("余额不足");
        }

        String settlementName = tradeCoin.getCoinName();
        transactionTemplate.execute(new TransactionCallbackWithoutResult() {
            @Override
            protected void doInTransactionWithoutResult(TransactionStatus transactionStatus) {
                ValueCheckUtils.notZero(cctAssetsMapper.decrease(userId, decreaseAmount, coinId), "余额不足");
//                String settlementName = userAssetsService.getSettlementNameById(settlementId);

                //审核
                if (amount.compareTo(withdrawAuditLimitAmount) >= 0) {
                    userAssetsCharge.setStatus(SettlementConst.USERASSETS_RECHARGE_AUDIT_NONE);
                } else {
                    userAssetsCharge.setStatus(SettlementConst.USERASSETS_RECHARGE_AUDIT_PASS);
                }

                ValueCheckUtils.notZero(insertIfNotNull(userAssetsCharge), "提现失败");
                String chargeId = userAssetsCharge.getId();
                AssetsTurnoverExtralParam extralParam = new AssetsTurnoverExtralParam();
                extralParam.setChargeId(chargeId);
                String turnoverDetail = "提现" + settlementName + "地址：" + toAddress + "，手续费：" + poundage;
                if (amount.compareTo(withdrawAuditLimitAmount) < 0) {
                    EthSendTransaction ethSendTransaction;
                    String fromAddress = globalConfigService.get(GlobalConfigService.Enum.Withdraw_Output_Address);
                    try {
                        ethSendTransaction = createEthTokenTransaction(tradeCoin, fromAddress, toAddress, amount);
                        if (!ethSendTransaction.hasError()) {
                            String transactionHash = ethSendTransaction.getTransactionHash();
                            WalletTransferRecord wtr = new WalletTransferRecord(
                                    settlementName, "资产提现", fromAddress, toAddress, amount, new Timestamp(System.currentTimeMillis()), transactionHash
                            );

                            insertIfNotNull(wtr);
                        }

                    } catch (InterruptedException | ExecutionException | IOException e) {
                        throw new BusinessException("提现失败");
                    }
                    userAssetsCharge.setTransactionHash(ethSendTransaction.getTransactionHash());
                    update(userAssetsCharge);
                }
            }
        });

        return ImmutableMap.of("amount", amount);
    }

    /**
     * 提现审核
     */
    public void auditWithDraw(UserAssetsCharge userAssetsCharge, Integer auditStatus, Subscriber subscriber) {
        if (userAssetsCharge.getStatus() != SettlementConst.USERASSETS_RECHARGE_AUDIT_NONE) {
            throw new BusinessException("该记录已审核");
        }

        BigDecimal withdrawAmount = userAssetsCharge.getRecordAmount();
        BigDecimal poundageAmount = userAssetsCharge.getPoundageAmount();

        BigDecimal backAmount = withdrawAmount.add(poundageAmount);
        String userId = userAssetsCharge.getUserId();

        String toAddress = userAssetsCharge.getToAddress();

        String auditStr = RechargeService.getAuditString(auditStatus);
        String remark = String.format("; 管理员: %s 于 %s 审核 %s", subscriber.getUserName(), MineDateUtils.getCurDateFormat(), auditStr);

        String errorMsg = "审核失败";
        String id = userAssetsCharge.getCoinId();

        TradeCoin tradeCoin = selectOneById(id, TradeCoin.class);
        transactionTemplate.execute(new TransactionCallbackWithoutResult() {
            @Override
            protected void doInTransactionWithoutResult(TransactionStatus transactionStatus) {
                userAssetsCharge.setRemark(userAssetsCharge.getRemark() + remark);
                if (auditStatus == SettlementConst.USERASSETS_RECHARGE_AUDIT_PASS) {
                    userAssetsCharge.setStatus(SettlementConst.USERASSETS_RECHARGE_AUDIT_PASS);


                    EthSendTransaction ethSendTransaction = null;
                    String fromAddress = globalConfigService.get(GlobalConfigService.Enum.Withdraw_Output_Address);
                    try {
                        ethSendTransaction = createEthTokenTransaction(tradeCoin, fromAddress, toAddress, withdrawAmount);
                    } catch (InterruptedException | ExecutionException | IOException e) {
                        throw new BusinessException("提现失败");
                    }
                    userAssetsCharge.setTransactionHash(ethSendTransaction.getTransactionHash());
                    userAssetsCharge.setFromAddress(fromAddress);
                    update(userAssetsCharge);
                } else {
                    ValueCheckUtils.notZero(cctAssetsMapper.increase(userId, backAmount, tradeCoin.getId()), errorMsg);
                    userAssetsCharge.setStatus(SettlementConst.USERASSETS_RECHARGE_AUDIT_UNPASS);

                }

            }
        });

    }


    private EthSendTransaction createEthTokenTransaction(TradeCoin tradeCoin, String fromAddress, String toAddress, BigDecimal withdrawAmount) throws InterruptedException, ExecutionException, IOException {
        environmentUtils.checkIfPro();

        String errorMsg = "提现失败";

        String privateKey = globalConfigService.get(GlobalConfigService.Enum.Withdraw_Output_Private_Key);
        String contractAddress = tradeCoin.getContractAddr();

        ValueCheckUtils.notEmptyString(fromAddress, "转出地址异常");
        ValueCheckUtils.notEmptyString(toAddress, "转入地址异常");
        ValueCheckUtils.notEmptyString(contractAddress, "合约地址异常");

        Integer decimalPoint = tradeCoin.getDecimalPoint();

        BigDecimal walletBalance = ethService.getERC20TokenBalance(fromAddress, contractAddress);
        walletBalance = walletBalance.divide(BigDecimal.valueOf(10).pow(decimalPoint));

        if (withdrawAmount.compareTo(walletBalance) > 0) {
            throw new BusinessException("余额不足");
        }

        //资产提取转账`
        return ethService.sendEthTokenTransaction(fromAddress, toAddress, contractAddress, withdrawAmount, privateKey);

    }
}
