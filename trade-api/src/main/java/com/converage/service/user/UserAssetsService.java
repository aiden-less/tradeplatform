package com.converage.service.user;

import com.google.common.collect.ImmutableMap;
import com.converage.architecture.exception.BusinessException;
import com.converage.architecture.service.BaseService;
import com.converage.entity.assets.CctAssets;
import com.converage.entity.market.TradeCoin;
import com.converage.entity.user.*;
import com.converage.mapper.user.AssetsTurnoverMapper;
import com.converage.mapper.user.CctAssetsMapper;
import com.converage.service.common.GlobalConfigService;
import com.converage.utils.DESUtils;
import com.converage.utils.ValueCheckUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.*;

import static com.converage.constance.SettlementConst.*;

@Service
public class UserAssetsService extends BaseService {
    private static final Logger logger = LoggerFactory.getLogger(UserAssetsService.class);

    @Autowired
    private CctAssetsMapper cctAssetsMapper;

    @Autowired
    private AssetsTurnoverMapper assetsTurnoverMapper;

    @Autowired
    private UserService userService;

    @Autowired
    private GlobalConfigService globalConfigService;

    public String getSettlementNameById(Integer settlementId) {
        switch (settlementId) {
            case SETTLEMENT_CURRENCY:
                return "";

            case SETTLEMENT_USDT:
                return "USDT";

            case SETTLEMENT_STATIC_CURRENCY:
                return "";

            case SETTLEMENT_DYNAMIC_CURRENCY:
                return "";

        }
        return null;
    }


    /**
     * 通过支付方式获取用户的资产
     *
     * @param settlementId 支付方式
     * @return
     * @throws BusinessException
     */
    public CctAssets getAssetsBySettlementId(String userId, Integer settlementId) throws BusinessException {
        Map<String, Object> whereMap = ImmutableMap.of(CctAssets.User_id + "=", userId, CctAssets.Coin_id + "=", settlementId);
        CctAssets cctAssets = selectOneByWhereMap(whereMap, CctAssets.class);
        ValueCheckUtils.notEmpty(cctAssets, "未找到资产记录");
        return cctAssets;
    }

    /**
     * 通过支付方式获取用户的资产
     *
     * @param settlementId 支付方式
     * @return
     * @throws BusinessException
     */
    public BigDecimal getAssetsAmountBySettlementId(String userId, Integer settlementId) throws BusinessException {
        Map<String, Object> whereMap = ImmutableMap.of(CctAssets.User_id + "=", userId, CctAssets.Coin_id + "=", settlementId);
        CctAssets cctAssets = selectOneByWhereMap(whereMap, CctAssets.class);
        ValueCheckUtils.notEmpty(cctAssets, "未找到资产记录");
        return cctAssets.getAssetsAmount();
    }

    /**
     * 增加用户资产
     *
     * @param userId       用户id
     * @param amount       数目（正数为加，负数为减）
     * @param settlementId 支付方式
     * @return
     */
    public Integer increaseUserAssets(String userId, BigDecimal amount, Integer settlementId) {
        return cctAssetsMapper.increaseUserAssets(userId, amount, settlementId);
    }

    /**
     * 减少用户资产
     *
     * @param userId       用户id
     * @param amount       数目（正数为加，负数为减）
     * @param settlementId 支付方式
     * @return
     */
    public Integer decreaseUserAssets(String userId, BigDecimal amount, Integer settlementId) {
        return cctAssetsMapper.decreaseUserAssets(userId, amount, settlementId);
    }



    /**
     * 扫码收款码查询信息
     *
     * @param walletAddress
     * @return
     */
    public Map<String, Object> getInfoByWalletAddress(String userId, String walletAddress) throws Exception {
        ValueCheckUtils.notEmpty(walletAddress, "转账地址不能为空");

        User sourceUser = selectOneById(userId, User.class);

        String[] str = DESUtils.decryptWithBase64(walletAddress, DESUtils.PASSWORD_CRYPT_KEY).split("-");
        ValueCheckUtils.notEmpty(str, "钱包地址有误");
        Integer settlementId = Integer.valueOf(str[0]);
        String walletAddressUserId = str[1];
        ValueCheckUtils.notEmpty(walletAddressUserId, "钱包地址异常");
        User targetUser = selectOneById(walletAddressUserId, User.class);
        ValueCheckUtils.notEmpty(targetUser, "用户不存在");
        if (targetUser.getId().equals(userId)) {
            throw new BusinessException("不能给自己转账");
        }

        BigDecimal balance = getAssetsAmountBySettlementId(userId, settlementId);

        TradeCoin wallet = selectOneByWhereString(TradeCoin.Settlement_id + "=", settlementId, TradeCoin.class);

        BigDecimal transferPoundageRate;
        if (sourceUser.getPhoneNumber().equals(targetUser.getPhoneNumber())) {
            transferPoundageRate = BigDecimal.ZERO;
        } else {
            transferPoundageRate = wallet.getTransferPoundageRate();
        }

        Map<String, Object> rMap = new HashMap<>();
        rMap.put("headPictureUrl", targetUser.getHeadPictureUrl());
        rMap.put("userAccount", targetUser.getUserAccount());
        rMap.put("phoneNumber", targetUser.getPhoneNumber().replaceAll("(\\d{3})\\d{4}(\\d{4})", "$1****$2"));
        rMap.put("settlementId", settlementId);
        rMap.put("balance", balance);
        rMap.put("walletAddress", walletAddress);
        rMap.put("transferPoundageRate", transferPoundageRate);
        rMap.put("minTransferAmount", wallet.getMinTransferAmount());
        rMap.put("maxTransferAmount", wallet.getMaxTransferAmount());

        return rMap;
    }

    /**
     * 输入账号查询信息
     *
     * @param userAccount
     * @param settlementId
     * @return
     */
    public Map<String, Object> getInfoByUserAccount(String userId, String userAccount, Integer settlementId) {
        User targetUser = selectOneByWhereString(User.User_account + "=", userAccount, User.class);
        ValueCheckUtils.notEmpty(targetUser, "用户不存在");
        User sourceUser = selectOneById(userId, User.class);
        if (targetUser.getId().equals(userId)) {
            throw new BusinessException("不能给自己转账");
        }
//        userService.ifBelongSameInviteSystem(sourceUser, targetUser);

        CctAssets targetCctAssets = getAssetsBySettlementId(targetUser.getId(), settlementId);
        BigDecimal balance = getAssetsAmountBySettlementId(userId, settlementId);

        TradeCoin wallet = selectOneByWhereString(TradeCoin.Settlement_id + "=", settlementId, TradeCoin.class);

        BigDecimal transferPoundageRate;
        if (sourceUser.getPhoneNumber().equals(targetUser.getPhoneNumber())) {
            transferPoundageRate = BigDecimal.ZERO;
        } else {
            transferPoundageRate = wallet.getTransferPoundageRate();
        }

        Map<String, Object> map = new HashMap<>();
        map.put("headPictureUrl", targetUser.getHeadPictureUrl());
        map.put("userAccount", userAccount);
        map.put("phoneNumber", targetUser.getPhoneNumber().replaceAll("(\\d{3})\\d{4}(\\d{4})", "$1****$2"));
        map.put("settlementId", settlementId);
        map.put("balance", balance);
        map.put("transferPoundageRate", transferPoundageRate);
        map.put("minTransferAmount", wallet.getMinTransferAmount());
        map.put("maxTransferAmount", wallet.getMaxTransferAmount());

        return map;
    }

    /**
     * 根据支付类型获取官方钱包地址
     *
     * @param settlementId
     * @return
     */
    public String getOfficialWalletAddress(Integer settlementId) {
        switch (settlementId) {
            case SETTLEMENT_CURRENCY:
                return globalConfigService.getByDb(GlobalConfigService.Enum.OFFICIAL_USDT_WALLETADDRESS);

            case SETTLEMENT_USDT:
                return globalConfigService.getByDb(GlobalConfigService.Enum.OFFICIAL_USDT_WALLETADDRESS);


            default:
                throw new BusinessException("错误支付类型");
        }

    }


    /**
     * 统计所有资产的流水数目
     *
     * @return
     */
    public Map<String, Object> countTurnoverAmount() {
        Map<String, Object> map = new HashMap<>();


        return map;
    }


}
