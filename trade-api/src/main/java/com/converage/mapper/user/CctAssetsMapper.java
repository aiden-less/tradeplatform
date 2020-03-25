package com.converage.mapper.user;

import com.converage.entity.transaction.CctOrder;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;

@Repository
public interface CctAssetsMapper {


    Integer increaseUserAssets(@Param("userId") String userId, @Param("amount") BigDecimal amount, @Param("settlementId") Integer settlementId);

    Integer decreaseUserAssets(@Param("userId") String userId, @Param("amount") BigDecimal amount, @Param("settlementId") Integer settlementId);

    Integer increase(@Param("userId") String userId, @Param("amount") BigDecimal amount, @Param("coinId") String coinId);

    Integer decrease(@Param("userId") String userId, @Param("amount") BigDecimal amount, @Param("coinId") String coinId);



    /**
     * 统计用户总数
     *
     * @return
     */
    Integer countUser(@Param("status") Integer status);

    /**
     * 统计今日注册用户
     *
     * @return
     */
    Integer countTodayRegisterUser(@Param("dateStr") String dateStr);


    /**
     * 统计资产充提累计
     *
     * @param settlementId
     * @return
     */
    BigDecimal countRWTurnover(@Param("recordType") Integer recordType, @Param("settlementId") Integer settlementId, @Param("dateStr") String dateStr);


    /**
     * 统计资产产出消耗累计
     *
     * @param settlementId
     * @param inOutType
     * @return
     */
    BigDecimal countPCAssets(@Param("settlementId") Integer settlementId, @Param("inOutType") Integer inOutType, @Param("dateStr") String dateStr);

    /**
     * 统计购买矿机累计
     *
     * @return
     */
    Integer countBuyMiningMachine();

    /**
     * 统计今日购买矿机
     *
     * @return
     */
    Integer countTodayBuyMiningMachine(@Param("dateStr") String dateStr);


    /**
     * 统计资产交易金额
     *
     * @param settlementId
     * @param dateStr
     * @return
     */
    BigDecimal countAssetsTransactionAmount(@Param("settlementId") Integer settlementId, @Param("dateStr") String dateStr);

    /**
     * 统计资产交易量
     *
     * @param settlementId
     * @param dateStr
     * @return
     */
    BigDecimal countAssetsTransactionNumber(@Param("settlementId") Integer settlementId, @Param("dateStr") String dateStr);


    /**
     * 完成资产交易
     *
     * @param cctOrder
     * @return
     */
    Integer finishTransaction(CctOrder cctOrder);

    /**
     * 减少订单剩余数量
     *
     * @param recordId
     * @param buyTransactionNumber
     * @return
     */
    Integer decreaseTransactionOrderNumber(@Param("recordId") String recordId, @Param("buyTransactionNumber") BigDecimal buyTransactionNumber);


    /**
     * 统计手续费
     *
     * @param recordType
     * @param settlementId
     * @param dateStr
     * @return
     */
    BigDecimal countRWPoundageTurnover(@Param("recordType") Integer recordType, @Param("settlementId") Integer settlementId, @Param("dateStr") String dateStr);

    BigDecimal countTCExchanges(@Param("settlementId") Integer settlementId, @Param("dateStr") String dateStr);

    BigDecimal countTurnoverAmount(@Param("turnoverType") Integer turnoverType, @Param("dateStr") String dateStr);

    Integer updateTransactionOrderNumber(@Param("ids") List<String> ids, @Param("assetsNumber") BigDecimal assetsNumber);
}
