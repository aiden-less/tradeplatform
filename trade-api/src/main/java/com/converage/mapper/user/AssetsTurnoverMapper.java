package com.converage.mapper.user;

import com.converage.architecture.dto.Pagination;
import com.converage.entity.user.AssetsTurnover;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;

@Repository
public interface AssetsTurnoverMapper {

    /**
     * 后台管理列表分页
     */
    List<AssetsTurnover> selectByPage(Pagination<AssetsTurnover> pagination);

    /**
     * 后台管理列表统计
     */
    AssetsTurnover selectTotal(Pagination<AssetsTurnover> pagination);


    /**
     * 查询用户的账单记录
     *
     * @param userId       用户id
     * @param turnoverType 账单记录
     * @param pagination   分页对象
     * @return
     */
    List<AssetsTurnover> listUserTurnover(@Param("userId") String userId, @Param("turnoverType") Integer turnoverType, @Param("settlementId") Integer settlementId, @Param("pagination") Pagination pagination);

    /**
     * 查询用户的资产记录（活动日志）
     *
     * @param userId
     * @param turnoverType
     * @param pagination
     * @return
     */
    List<AssetsTurnover> listUserSettlementTurnover(@Param("userId") String userId, @Param("turnoverType") Integer turnoverType, @Param("settlementId") Integer settlementId, @Param("inOutType") Integer inOutType, @Param("pagination") Pagination pagination);


    /**
     * 统计活动日志算数
     *
     * @param userId
     * @param turnoverType
     * @param dateStr
     * @return
     */
    Integer selectCount(@Param("userId") String userId, @Param("turnoverType") Integer turnoverType, @Param("dateStr") String dateStr);

    BigDecimal countReward(@Param("turnoverTitle") String turnoverTitle, @Param("dateStr") String dateStr);

}
