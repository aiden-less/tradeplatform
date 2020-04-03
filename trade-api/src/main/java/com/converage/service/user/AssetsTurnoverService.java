package com.converage.service.user;

import com.converage.architecture.dto.Pagination;
import com.converage.architecture.dto.TotalResult;
import com.converage.architecture.exception.BusinessException;
import com.converage.architecture.service.BaseService;
import com.converage.constance.SettlementConst;
import com.converage.entity.user.AssetsTurnover;
import com.converage.entity.user.AssetsTurnoverExtralParam;
import com.converage.mapper.user.AssetsTurnoverMapper;
import com.converage.service.assets.UserAssetsService;
import com.converage.utils.BigDecimalUtils;
import com.converage.utils.ValueCheckUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Objects;

import static com.converage.constance.AssetTurnoverConst.*;
import static com.converage.constance.SettlementConst.USERASSETS_RECHARGE_AUDIT_NONE;
import static com.converage.constance.SettlementConst.USERASSETS_RECHARGE_AUDIT_PASS;
import static com.converage.constance.SettlementConst.USERASSETS_RECHARGE_AUDIT_UNPASS;

@Service
public class AssetsTurnoverService extends BaseService {

    @Autowired
    private UserAssetsService userAssetsService;

    @Autowired
    private AssetsTurnoverMapper assetsTurnoverMapper;


    /**
     * 创建流水记录
     *
     * @param userId         用户id
     * @param turnoverType   流水类型 TURNOVER_TYPE_*
     * @param turnoverAmount 流水数目
     * @param sourceId       流水源id   (userId) 系统则为 0
     * @param targetId       流水目标id (userId) 系统则为 0
     * @param settlementId   支付方式
     * @param detailStr      详细信息
     */
    public void createAssetsTurnover(String userId, Integer turnoverType, BigDecimal turnoverAmount, String sourceId, String targetId, Integer settlementId, String detailStr) throws BusinessException {
        AssetsTurnover assetsTurnover = new AssetsTurnover();
        assetsTurnover.setUserId(userId);
        assetsTurnover.setTurnoverTitle(turnoverNameByType(turnoverType));
        assetsTurnover.setTurnoverType(turnoverType);
        assetsTurnover.setTurnoverAmount(turnoverAmount);
        assetsTurnover.setSourceId(sourceId);
        assetsTurnover.setTargetId(targetId);
        assetsTurnover.setSettlementId(settlementId);
        assetsTurnover.setDetailStr(detailStr);

//        CctAssets userAssets = userAssetsService.getAssetsByUserId(userId);
//        BigDecimal userAssetsAmount = userAssetsService.getAssetsBySettlementId(userAssets, settlementId);

//        if (Objects.equals(assetsTurnover.getSourceId(), userId)) {
//            assetsTurnover.setAfterAmount(userAssetsAmount.subtract(turnoverAmount));
//        } else {
//            assetsTurnover.setAfterAmount(userAssetsAmount.add(turnoverAmount));
//        }

        ValueCheckUtils.notZero(insertIfNotNull(assetsTurnover), "流水记录创建失败");
    }

    /**
     * 创建流水记录
     *
     * @param userId         用户id
     * @param turnoverType   流水类型 TURNOVER_TYPE_*
     * @param turnoverAmount 流水数目
     * @param sourceId       流水源id   (userId) 系统则为 0
     * @param targetId       流水目标id (userId) 系统则为 0
     * @param settlementId   支付方式
     * @param detailStr      详细信息
     */
    public void createAssetsTurnover(String userId, Integer turnoverType, BigDecimal turnoverAmount, String sourceId, String targetId, Integer settlementId, String detailStr, AssetsTurnoverExtralParam extralParam) throws BusinessException {
        AssetsTurnover assetsTurnover = new AssetsTurnover();
        assetsTurnover.setUserId(userId);
        assetsTurnover.setTurnoverTitle(turnoverNameByType(turnoverType, extralParam));
        assetsTurnover.setTurnoverType(turnoverType);
        assetsTurnover.setTurnoverAmount(turnoverAmount);
        assetsTurnover.setSourceId(sourceId);
        assetsTurnover.setTargetId(targetId);
        assetsTurnover.setSettlementId(settlementId);
        assetsTurnover.setDetailStr(detailStr);

//        CctAssets userAssets = userAssetsService.getAssetsByUserId(userId);
//        BigDecimal userAssetsAmount = userAssetsService.getAssetsBySettlementId(userAssets, settlementId);

//        if (Objects.equals(assetsTurnover.getSourceId(), userId)) {
//            assetsTurnover.setAfterAmount(userAssetsAmount.subtract(turnoverAmount));
//        } else {
//            assetsTurnover.setAfterAmount(userAssetsAmount.add(turnoverAmount));
//        }

        ValueCheckUtils.notZero(insertIfNotNull(assetsTurnover), "流水记录创建失败");
    }


    /**
     * 创建流水记录
     *
     * @param userId         用户id
     * @param turnoverType   流水类型 TURNOVER_TYPE_*
     * @param turnoverAmount 流水数目
     * @param sourceId       流水源id   (userId) 系统则为 0
     * @param targetId       流水目标id (userId) 系统则为 0
     * @param settlementId   支付方式
     * @param detailStr      详细信息
     */
    public void createAssetsTurnover(String userId, Integer turnoverType, BigDecimal turnoverAmount, String sourceId, String targetId, String remark, Integer settlementId, String detailStr, AssetsTurnoverExtralParam extralParam) throws BusinessException {
        AssetsTurnover assetsTurnover = new AssetsTurnover();
        assetsTurnover.setUserId(userId);
        assetsTurnover.setTurnoverTitle(turnoverNameByType(turnoverType));
        assetsTurnover.setTurnoverType(turnoverType);
        assetsTurnover.setTurnoverAmount(turnoverAmount);
        assetsTurnover.setSourceId(sourceId);
        assetsTurnover.setTargetId(targetId);
        String chargeId = extralParam.getChargeId();
        if (StringUtils.isEmpty(chargeId)) {
            throw new BusinessException("参数缺失");
        }
        assetsTurnover.setChargeId(chargeId);
        assetsTurnover.setSettlementId(settlementId);
        assetsTurnover.setDetailStr(detailStr);
        assetsTurnover.setRemark(remark);
//        BigDecimal userAssetsAmount = userAssetsService.getAssetsBySettlementId(userAssets, settlementId);
//
//        if (Objects.equals(assetsTurnover.getSourceId(), userId)) {
//            assetsTurnover.setAfterAmount(userAssetsAmount.subtract(turnoverAmount));
//        } else {
//            assetsTurnover.setAfterAmount(userAssetsAmount.add(turnoverAmount));
//        }

        ValueCheckUtils.notZero(insertIfNotNull(assetsTurnover), "流水记录创建失败");
    }


    public TotalResult<AssetsTurnover> getByPage(Pagination<AssetsTurnover> pagination) {
        List<AssetsTurnover> assetsTurnovers = assetsTurnoverMapper.selectByPage(pagination);
        assetsTurnovers.forEach(assetsTurnover -> {
            if (Objects.equals(assetsTurnover.getSourceId(), COMPANY_ID)) {
                assetsTurnover.setInOutStr("+");
            } else {
                assetsTurnover.setInOutStr("-");
            }
        });
        return new TotalResult<>(assetsTurnoverMapper.selectTotal(pagination), assetsTurnovers);
    }


    /**
     * 查询用户的账单记录
     *
     * @param userId       用户id
     * @param turnoverType 流水记录
     * @param pagination   分页参数
     * @return
     */
    public List<AssetsTurnover> listUserTurnover(String userId, Integer turnoverType, Integer settlementId, Pagination pagination) {
        List<AssetsTurnover> turnoverList = assetsTurnoverMapper.listUserTurnover(userId, turnoverType, settlementId, pagination);
        turnoverList.forEach(turnover -> {
            turnover.setIcon(getIconBySettlementId(turnover.getTurnoverType()));
            turnover.setSettlementUnit(getSettlementNameById(turnover.getSettlementId()));
            turnover.setSettlementName(getSettlementCnNameById(turnover.getSettlementId()));
            if (Objects.equals(turnover.getSourceId(), userId)) {
                turnover.setTurnoverAmount(BigDecimalUtils.transfer2Negative(turnover.getTurnoverAmount()));
                turnover.setInOutStr("支出");
            } else {
                turnover.setInOutStr("收益");
            }

            Integer turnoverType_ = turnover.getTurnoverType();
            if (turnoverType_ == TURNOVER_TYPE_RECHARGE || turnoverType_ == TURNOVER_TYPE_WITHDRAW) {
                Integer chargeStatus = turnover.getChargeStatus();
                if (chargeStatus != null) {
                    switch (chargeStatus
                            ) {
                        case USERASSETS_RECHARGE_AUDIT_NONE:
                            turnover.setRemark("审核中");
                            break;

                        case USERASSETS_RECHARGE_AUDIT_PASS:
                            turnover.setRemark("已成功");
                            break;

                        case USERASSETS_RECHARGE_AUDIT_UNPASS:
                            turnover.setRemark("不通过");
                            break;
                    }
                }

            } else if (turnoverType_ == TURNOVER_TYPE_TRANSFER) {
                turnover.setRemark("已成功");
            }

        });
        return turnoverList;
    }

    /**
     * 查看用户的活动日志
     *
     * @param userId
     * @param turnoverType
     * @param settlementId
     * @param inOutType
     * @return
     */
    public List<AssetsTurnover> settlementList(String userId, Integer turnoverType, Integer settlementId, Integer inOutType, Pagination pagination) {
        List<AssetsTurnover> turnoverList = assetsTurnoverMapper.listUserSettlementTurnover(userId, turnoverType, settlementId, inOutType, pagination);
        turnoverList.forEach(turnover -> {
            String settlementUnit = getSettlementNameById(turnover.getSettlementId());
            turnover.setIcon(getIconBySettlementId(turnover.getTurnoverType()));
            turnover.setSettlementUnit(settlementUnit);
            turnover.setSettlementName(getSettlementCnNameById(turnover.getSettlementId()));
            if (Objects.equals(turnover.getSourceId(), userId)) {
                turnover.setInOutStr(settlementUnit + "支出");
                turnover.setTurnoverAmount(BigDecimalUtils.transfer2Negative(turnover.getTurnoverAmount()));
            } else {
                turnover.setInOutStr(settlementUnit + "收益");
            }

        });
        return turnoverList;
    }

    /**
     * 根据支付id获取支付描述
     *
     * @param turnoverType
     * @return
     */
    public String getIconBySettlementId(Integer turnoverType) {
        switch (turnoverType) {
            case TURNOVER_TYPE_PACKAGE:
                return "https://taste-common-img.oss-cn-hongkong.aliyuncs.com/2019-07-10/6c594fda-337e-4f89-8f74-efc4081066ae.png";

            case TURNOVER_TYPE_TRANSACTION:
                return "https://taste-common-img.oss-cn-hongkong.aliyuncs.com/2019-07-10/57ec8473-1bee-451b-a35c-1cbcab21739d.png";

            case TURNOVER_TYPE_SHARE_OTHER_MINING:
                return "https://taste-common-img.oss-cn-hongkong.aliyuncs.com/2019-07-10/afad4db9-63e3-4059-94fc-49ea4c8210cf.png";

            case TURNOVER_TYPE_SHARE_SPEED_MINING:
                return "https://taste-common-img.oss-cn-hongkong.aliyuncs.com/2019-07-10/62e34d4d-5746-4b90-8dca-fa7d8df7f0fb.png";

            case TURNOVER_TYPE_MINING:
                return "https://taste-common-img.oss-cn-hongkong.aliyuncs.com/2019-07-10/c07c26ef-0458-4688-b77f-6119a3cec168.png";

            case TURNOVER_TYPE_MISSION_REWARD:
                return "https://taste-common-img.oss-cn-hongkong.aliyuncs.com/2019-07-10/5f59c0bd-7054-4b6b-a3d4-5b923b2ce676.png";

        }
        return "";
    }

    /**
     * 根据支付id获取支付描述
     *
     * @param settlementId
     * @return
     */
    public String getSettlementNameById(Integer settlementId) {
        switch (settlementId) {
            case SettlementConst.SETTLEMENT_CURRENCY:
                return "TC";

            case SettlementConst.SETTLEMENT_STATIC_CURRENCY:
                return "配送品值";

            case SettlementConst.SETTLEMENT_DYNAMIC_CURRENCY:
                return "赠送品值";

            case SettlementConst.SETTLEMENT_USDT:
                return "USDT";

            case SettlementConst.SETTLEMENT_INTEGRAL:
                return "积分";

            case SettlementConst.SETTLEMENT_COMPUTING_POWER:
                return "品值";
        }
        return "";
    }

    public String turnoverNameByType(Integer turnoverType) {
        return turnoverNameByType(turnoverType, null);
    }


    public String turnoverNameByType(Integer turnoverType, AssetsTurnoverExtralParam extralParam) {
        switch (turnoverType) {
            case TURNOVER_TYPE_RECHARGE:
                return "资产充值";

            case TURNOVER_TYPE_WITHDRAW:
                return "资产提现";

            case TURNOVER_TYPE_TRANSFER:
                return "资产转账";

            case TURNOVER_TYPE_GOODS:
                return "购买商品";

            case TURNOVER_TYPE_PACKAGE:
                return "购买礼包";

            case TURNOVER_TYPE_MINING:
//                String machineName = extralParam == null ? "" : extralParam.getMiningMachineName();
//                return machineName + "每日释放收益";
                return "品值收益";

            case TURNOVER_TYPE_SHARE_SPEED_MINING:
                return "分享收益";

            case TURNOVER_TYPE_SHARE_OTHER_MINING:
                return "共享释放";

            case TURNOVER_TYPE_SHARE_TEAM_MINING:
                return "团队收益";

            case TURNOVER_TYPE_TRANSACTION:
                return "闪兑交易";

            case TURNOVER_TYPE_QRCODE:
                return "收益领取";

            case TURNOVER_TYPE_MISSION_REWARD:
                String missionName = extralParam == null ? "" : extralParam.getMissionName();
                return missionName + "奖励";

            case TURNOVER_TYPE_APPLET_REWARD:
                return "应用奖励";


        }
        return "";
    }

    public String getSettlementCnNameById(Integer settlementId) {
        switch (settlementId) {
            case SettlementConst.SETTLEMENT_CURRENCY:
                return "TC";

            case SettlementConst.SETTLEMENT_USDT:
                return "USDT";

            case SettlementConst.SETTLEMENT_ORE:
                return "矿石";

            case SettlementConst.SETTLEMENT_INTEGRAL:
                return "积分";

            case SettlementConst.SETTLEMENT_COMPUTING_POWER:
                return "颜值";
        }
        return "";
    }


    public static String getInOutType(int type) {
        switch (type) {
            case PAY:
                return "支出";
            case INCOME:
                return "收入";
            default:
                return "位置支出收入类型";
        }
    }

}
