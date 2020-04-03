package com.converage.service.assets;

import com.converage.mapper.user.CctAssetsMapper;
import com.google.common.collect.ImmutableMap;
import com.converage.architecture.exception.BusinessException;
import com.converage.architecture.service.BaseService;
import com.converage.constance.SettlementConst;
import com.converage.entity.assets.CctFinanceLog;
import com.converage.entity.sys.Subscriber;
import com.converage.entity.market.TradeCoin;
import com.converage.entity.user.AssetsTurnover;
import com.converage.entity.user.AssetsTurnoverExtralParam;
import com.converage.entity.user.User;
import com.converage.service.common.AliOSSBusiness;
import com.converage.service.common.GlobalConfigService;
import com.converage.service.user.AssetsTurnoverService;
import com.converage.service.user.UserService;
import com.converage.utils.MineDateUtils;
import com.converage.utils.ValueCheckUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallbackWithoutResult;
import org.springframework.transaction.support.TransactionTemplate;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static com.converage.constance.AssetTurnoverConst.*;
import static com.converage.constance.SettlementConst.*;

@Service
public class RechargeService extends BaseService {

    @Autowired
    private TransactionTemplate transactionTemplate;

    @Autowired
    private AliOSSBusiness aliOSSBusiness;

    @Autowired
    private CctAssetsMapper cctAssetsMapper;

    @Autowired
    private AssetsTurnoverService assetsTurnoverService;

    @Autowired
    private GlobalConfigService globalConfigService;

    @Autowired
    private UserService userService;

    /**
     * 申请充值
     *
     * @param userId       用户Id
     * @param settlementId 支付类型
     */
    public Map<String, Object> applyRecharge(String userId, Integer settlementId, BigDecimal rechargeAmount, String fromAddress, MultipartFile rechargeFile) throws IOException {
//        throw new BusinessException("手动充值已停用");

        User user = selectOneById(userId, User.class);
        if (!user.getIfCanRecharge()) {
            throw new BusinessException("充值功能已被禁用");
        }
        String sendWalletAddress = "";
        ValueCheckUtils.notEmpty(rechargeAmount, "请输入转账金额");
        ValueCheckUtils.notEmpty(rechargeFile, "请上传充值截图");
        ValueCheckUtils.notEmptyString(fromAddress, "请输入您的转出地址");


        if (rechargeAmount.compareTo(BigDecimal.ZERO) < 0) {
            throw new BusinessException("充值金额必须大于0");
        }

//        //TODO 充值源地址审核
//        Web3j web3j = Web3j.build(new HttpService("https://mainnet.infura.io/v3/eac43464a30d4fcc890f3656e8290e45"));
//        Request<?, EthGetBalance> ethGetBalanceRequest = web3j.ethGetBalance("0x" + fromAddress, null);
//        EthGetBalance ethGetBalance = ethGetBalanceRequest.send();
//        ValueCheckUtils.notEmpty(ethGetBalance, "您的钱包地址有误，请确认");


        TradeCoin wallet = selectOneByWhereString(TradeCoin.Id + "=", settlementId, TradeCoin.class);
        if (!wallet.getIfRecharge()) {
            throw new BusinessException("该资产暂不支持充值");
        }

        if (settlementId == SETTLEMENT_CURRENCY) {
            sendWalletAddress = globalConfigService.get(GlobalConfigService.Enum.OFFICIAL_CURRENCY_WALLETADDRESS);
        } else if (settlementId == SETTLEMENT_USDT) {
            sendWalletAddress = globalConfigService.get(GlobalConfigService.Enum.OFFICIAL_USDT_WALLETADDRESS);
        } else {
            throw new BusinessException("充值类型异常");
        }

        CctFinanceLog cctFinanceLog = new CctFinanceLog();
//        cctFinanceLog.setRecordPic(aliOSSBusiness.uploadCommonPic(rechargeFile));
        cctFinanceLog.setUserId(userId);
        cctFinanceLog.setRecordType(SettlementConst.USERASSETS_RECHARGE);
        cctFinanceLog.setStatus(SettlementConst.USERASSETS_RECHARGE_AUDIT_NONE);
        cctFinanceLog.setRecordAmount(rechargeAmount);
        String detailStr = "转出地址：" + fromAddress + "，转入地址：" + sendWalletAddress;
        cctFinanceLog.setRemark(detailStr);
        cctFinanceLog.setFromAddress(fromAddress);
        cctFinanceLog.setToAddress(sendWalletAddress);


        String errorMsg = "充值失败";
        transactionTemplate.execute(new TransactionCallbackWithoutResult() {
            @Override
            protected void doInTransactionWithoutResult(TransactionStatus transactionStatus) {
                ValueCheckUtils.notZero(insertIfNotNull(cctFinanceLog), errorMsg);
                String chargeId = cctFinanceLog.getId();
                AssetsTurnoverExtralParam extralParam = new AssetsTurnoverExtralParam();
                extralParam.setChargeId(chargeId);
                assetsTurnoverService.createAssetsTurnover(
                        userId, TURNOVER_TYPE_RECHARGE, rechargeAmount, COMPANY_ID, userId, "审核中", settlementId,
                        detailStr, extralParam
                );
            }
        });
        return ImmutableMap.of("settlementId", settlementId);
    }


    public void auditRecharge(CctFinanceLog cctFinanceLog, Integer auditStatus) throws IOException {
        if (cctFinanceLog.getStatus() != SettlementConst.USERASSETS_RECHARGE_AUDIT_NONE) {
            throw new BusinessException("该记录已审核");
        }

        String chargeId = cctFinanceLog.getId();
        String userId = cctFinanceLog.getUserId();
        String coinId = cctFinanceLog.getCoinId();

        Map<String, Object> whereMap = ImmutableMap.of(
                CctFinanceLog.User_id + "=", cctFinanceLog.getUserId(),
                CctFinanceLog.Status + "=", USERASSETS_RECHARGE_AUDIT_PASS
        );
        List<String> fieldList = Arrays.asList(CctFinanceLog.Id);
        List<CctFinanceLog> cctFinanceLogs = selectiveListByWhereMap(fieldList, whereMap, CctFinanceLog.class);
        Boolean finishMission = false;
        if (cctFinanceLogs.size() == 0) {
            finishMission = true;
        }

        String errorMsg = "审核失败";
        Boolean finalFinishMission = finishMission;
        transactionTemplate.execute(new TransactionCallbackWithoutResult() {
            @Override
            protected void doInTransactionWithoutResult(TransactionStatus transactionStatus) {

                BigDecimal amount = cctFinanceLog.getRecordAmount();
                if (auditStatus == USERASSETS_RECHARGE_AUDIT_PASS) {
                    cctFinanceLog.setStatus(USERASSETS_RECHARGE_AUDIT_PASS);
                    cctFinanceLog.setRecordAmount(amount);

                    ValueCheckUtils.notZero(cctAssetsMapper.increase(userId, amount, coinId), errorMsg);
                } else {
                    cctFinanceLog.setStatus(USERASSETS_RECHARGE_AUDIT_UNPASS);
                }


                ValueCheckUtils.notZero(update(cctFinanceLog), errorMsg);

            }
        });
    }

    /**
     * 审核充值
     *
     * @param recordId
     * @param auditStatus
     */
    public void auditRecharge(String recordId, Integer auditStatus, Subscriber subscriber) throws IOException {
        CctFinanceLog cctFinanceLog = selectOneById(recordId, CctFinanceLog.class);
        ValueCheckUtils.notEmpty(cctFinanceLog, "未找到充值记录");

        BigDecimal recordAmount = cctFinanceLog.getRecordAmount();
        if (cctFinanceLog.getStatus() != SettlementConst.USERASSETS_RECHARGE_AUDIT_NONE) {
            throw new BusinessException("该记录已审核");
        }

        if (!cctFinanceLog.getIfConfirm()) {
            throw new BusinessException("该记录未确认，请等待交易确认后再审核");
        }


        String userId = cctFinanceLog.getUserId();


        String auditStr = getAuditString(auditStatus);
        String remark = "";
        if (subscriber != null) {
            remark = String.format("; 管理员: %s 于 %s 审核 %s", subscriber.getUserName(), MineDateUtils.getCurDateFormat(), auditStr);
        }

        String coinId = cctFinanceLog.getCoinId();
        cctFinanceLog.setRemark(cctFinanceLog.getRemark() + remark);

        String errorMsg = "审核失败";

        AssetsTurnover assetsTurnover = selectOneByWhereString(AssetsTurnover.Charge_id + "=", recordId, AssetsTurnover.class);


        String finalRemark = remark;
        transactionTemplate.execute(new TransactionCallbackWithoutResult() {
            @Override
            protected void doInTransactionWithoutResult(TransactionStatus transactionStatus) {

                cctFinanceLog.setRemark(cctFinanceLog.getRemark() + finalRemark);
                if (auditStatus == USERASSETS_RECHARGE_AUDIT_PASS) {
                    cctFinanceLog.setStatus(USERASSETS_RECHARGE_AUDIT_PASS);
                    cctFinanceLog.setRecordAmount(recordAmount);

                    assetsTurnover.setTurnoverAmount(recordAmount);

                    ValueCheckUtils.notZero(updateIfNotNull(assetsTurnover), errorMsg);
                    ValueCheckUtils.notZero(cctAssetsMapper.increase(userId, recordAmount, coinId), errorMsg);
                } else {
                    cctFinanceLog.setStatus(USERASSETS_RECHARGE_AUDIT_UNPASS);
                }


                ValueCheckUtils.notZero(update(cctFinanceLog), errorMsg);


            }
        });
    }

    /**
     * 充值资产
     *
     * @param userName
     * @param userId
     * @param rechargeAmount
     * @param rechargeSettlementId
     */
    public void rechargeAssets(String userName, String userId, BigDecimal rechargeAmount, Integer rechargeSettlementId) {
//        if (BigDecimal.ZERO.compareTo(rechargeAmount) > 0) {
//            throw new BusinessException("充值金额必须大于0");
//        }


//        transactionTemplate.execute(new TransactionCallbackWithoutResult() {
//            @Override
//            protected void doInTransactionWithoutResult(TransactionStatus transactionStatus) {
//                ValueCheckUtils.notZero(userAssetsService.increaseUserAssets(userId, rechargeAmount, rechargeSettlementId), "充值失败");
//
//                CctFinanceLog userAssetsCharge = new CctFinanceLog();
//                userAssetsCharge.setUserId(userId);
//                userAssetsCharge.setRecordType(SettlementConst.USERASSETS_RECHARGE);
//                userAssetsCharge.setRecordAmount(rechargeAmount);
//                userAssetsCharge.setSettlementId(rechargeSettlementId);
//                userAssetsCharge.setStatus(USERASSETS_RECHARGE_AUDIT_PASS);
//                userAssetsCharge.setRemark("后台用户[" + userName + "],进行充值");
//                ValueCheckUtils.notZero(insertIfNotNull(userAssetsCharge), "充值失败");
//
//            }
//        });
    }

    /**
     * 充值资产
     *
     * @param userName
     * @param userId
     */
    public void rechargeMachine(String userName, String userId, String machineId) {

    }

    public static String getAuditString(int status) {
        String auditByStatus = getAuditByStatus(status);
        if (auditByStatus == null) {
            throw new BusinessException("错误的状态");
        }
        return auditByStatus;
    }

    public static String getAuditByStatus(int status) {
        switch (status) {
            case USERASSETS_RECHARGE_AUDIT_PASS:
                return "通过";
            case USERASSETS_RECHARGE_AUDIT_UNPASS:
                return "拒绝";
            default:
                return null;
        }
    }


    public static String getTurnoverType(int type) {
        switch (type) {
            case TURNOVER_TYPE_RECHARGE:
                return "充值";
            case TURNOVER_TYPE_WITHDRAW:
                return "提现";
            case TURNOVER_TYPE_TRANSFER:
                return "转账";
            default:
                throw new BusinessException("错误的类型");
        }
    }
}
