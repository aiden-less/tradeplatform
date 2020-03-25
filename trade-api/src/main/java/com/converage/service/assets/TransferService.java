package com.converage.service.assets;

import com.google.common.collect.ImmutableMap;
import com.converage.architecture.exception.BusinessException;
import com.converage.architecture.service.BaseService;
import com.converage.constance.SettlementConst;
import com.converage.entity.assets.UserAssetsCharge;
import com.converage.entity.market.TradeCoin;
import com.converage.entity.user.User;
import com.converage.service.common.GlobalConfigService;
import com.converage.service.user.AssetsTurnoverService;
import com.converage.service.user.UserAssetsService;
import com.converage.service.user.UserService;
import com.converage.utils.DESUtils;
import com.converage.utils.EncryptUtils;
import com.converage.utils.ValueCheckUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallbackWithoutResult;
import org.springframework.transaction.support.TransactionTemplate;

import java.math.BigDecimal;
import java.util.Map;

import static com.converage.constance.AssetTurnoverConst.TURNOVER_TYPE_TRANSFER;

@Service
public class TransferService extends BaseService {

    @Autowired
    private TransactionTemplate transactionTemplate;

    @Autowired
    private UserAssetsService userAssetsService;

    @Autowired
    private AssetsTurnoverService assetsTurnoverService;

    @Autowired
    private UserService userService;

    @Autowired
    private GlobalConfigService globalConfigService;

    /**
     * 申请转账
     *
     * @param userId
     * @param amount
     * @param walletAddress
     */
    public Map<String, Object> applyTransfer(String userId, BigDecimal amount, String walletAddress, String payPassword) throws Exception {
        User sourceUser = selectOneById(userId, User.class);
        if (!sourceUser.getIfCanTransfer()) {
            throw new BusinessException("转账功能已被禁用");
        }
        String userPayPassword = sourceUser.getPayPassword();

        ValueCheckUtils.notEmpty(userPayPassword, "请先设置支付密码");
        ValueCheckUtils.notEmpty(payPassword, "支付密码不能为空");

        if (!EncryptUtils.md5Password(payPassword).equals(userPayPassword)) {
            throw new BusinessException("支付密码错误");
        }

        String[] str = DESUtils.decryptWithBase64(walletAddress, DESUtils.PASSWORD_CRYPT_KEY).split("-");
        ValueCheckUtils.notEmpty(str, "钱包地址有误");
        Integer settlementId = Integer.valueOf(str[0]);
        String walletAddressUserId = str[1];

        BigDecimal sourceUserAssetsAmount = userAssetsService.getAssetsAmountBySettlementId(userId, settlementId);

        TradeCoin wallet = selectOneByWhereString(TradeCoin.Settlement_id + "=", settlementId, TradeCoin.class);
        BigDecimal transferRate = wallet.getTransferPoundageRate().multiply(BigDecimal.valueOf(0.01));
        BigDecimal transferGas = amount.multiply(transferRate);

        User targetUser = selectOneById(walletAddressUserId, User.class);
        ValueCheckUtils.notEmpty(targetUser, "用户不存在");
        String targetUserId = targetUser.getId();

        if (sourceUser.getPhoneNumber().equals(targetUser.getPhoneNumber())) {
            transferGas = BigDecimal.ZERO;
        }

        BigDecimal decreaseAmount = amount.add(transferGas);


        if (sourceUserAssetsAmount.compareTo(decreaseAmount) < 0) {
            throw new BusinessException("余额不足");
        }


        BigDecimal transferMin = wallet.getMinTransferAmount();
        if (amount.compareTo(transferMin) < 0) {
            throw new BusinessException("转账最低金额为：" + transferMin.stripTrailingZeros().toPlainString());
        }

        BigDecimal transferMax = wallet.getMaxTransferAmount();
        if (transferMax.compareTo(amount) < 0) {
            throw new BusinessException("转账最大额度为：" + transferMax.stripTrailingZeros().toPlainString());
        }


        if (userId.equals(targetUserId)) {
            throw new BusinessException("不能向自己转账");
        }

        UserAssetsCharge userAssetsCharge = new UserAssetsCharge();
        userAssetsCharge.setUserId(userId);

        //扣除手续费
        BigDecimal withdrawRate = wallet.getWithDrawPoundageRate().divide(BigDecimal.valueOf(100));
        BigDecimal poundage = amount.multiply(withdrawRate);
        if (sourceUser.getPhoneNumber().equals(targetUser.getPhoneNumber())) {
            poundage = BigDecimal.ZERO;
        }
        userAssetsCharge.setRecordAmount(amount);
        userAssetsCharge.setPoundageAmount(poundage);
        userAssetsCharge.setRecordType(SettlementConst.USERASSETS_TRANSFER);
        userAssetsCharge.setSettlementId(settlementId);
        userAssetsCharge.setStatus(SettlementConst.USERASSETS_RECHARGE_AUDIT_PASS);
        userAssetsCharge.setRemark("转账金额：" + amount + " 转出用户：" + sourceUser.getUserAccount() + "，转入用户：" + targetUser.getUserAccount() + "，手续费：" + poundage);


        String gasStr = "，手续费为：" + transferGas.stripTrailingZeros().toPlainString();

        //转账
        Integer finalSettlementId = settlementId;
        transactionTemplate.execute(new TransactionCallbackWithoutResult() {
            @Override
            protected void doInTransactionWithoutResult(TransactionStatus transactionStatus) {
                ValueCheckUtils.notZero(insertIfNotNull(userAssetsCharge), "转账失败");

                ValueCheckUtils.notZero(userAssetsService.decreaseUserAssets(userId, decreaseAmount, finalSettlementId), "转账失败，请稍后再试");
                assetsTurnoverService.createAssetsTurnover(
                        userId, TURNOVER_TYPE_TRANSFER, decreaseAmount, userId, targetUserId, finalSettlementId,
                        "转账给用户：" + targetUser.getUserAccount() + "到账数目为：" + amount.stripTrailingZeros().toPlainString() + gasStr
                );

                ValueCheckUtils.notZero(userAssetsService.increaseUserAssets(targetUserId, amount, finalSettlementId), "转账失败，请稍后再试");
                assetsTurnoverService.createAssetsTurnover(
                        targetUserId, TURNOVER_TYPE_TRANSFER, amount, userId, targetUserId, finalSettlementId,
                        "用户：" + sourceUser.getUserAccount() + " 向您转账，转账数目为：" + amount.stripTrailingZeros().toPlainString()
                );
            }
        });
        return ImmutableMap.of("userAccount", targetUser.getUserName(), "amount", amount, "settlementId", settlementId);
    }
}
