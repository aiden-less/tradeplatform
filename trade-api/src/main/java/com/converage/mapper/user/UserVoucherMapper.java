package com.converage.mapper.user;

import com.converage.entity.user.UserVoucherRecord;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserVoucherMapper {

    /**
     * 查询用户指定的卡券信息
     *
     * @param userId
     * @return
     */
    List<UserVoucherRecord> listUserVoucher(@Param("userId") String userId);

    /**
     * 查找用户指定的卡券信息
     *
     * @param userId
     * @param voucherId
     * @return
     */
    UserVoucherRecord getUserVoucher(@Param("userId") String userId, @Param("voucherId") String voucherId);

    /**
     * 扣除用户的卡券数量
     *
     * @param userVoucherRecordId
     * @return
     */
    Integer decreaseQuantity(@Param("userVoucherRecordId") String userVoucherRecordId);


    /**
     * 作废逾期卡券
     *
     * @param ids
     */
    void updateExpireVoucher(@Param("ids") List<String> ids);

    /**
     * 查询当天逾期的卡券
     *
     * @param expireTime
     * @return
     */
    List<UserVoucherRecord> listExpireVoucher(@Param("expireTime") String expireTime);

    /**
     * 查询商品对应的卡券
     *
     * @param userId
     * @return
     */
    List<UserVoucherRecord> listGoodsVoucher(@Param("userId") String userId);

    /**
     * 作废卡券
     * @param userId
     * @param voucherId
     * @return
     */
    Integer cancelVoucherValidity(@Param("userId") String userId, @Param("voucherId") String voucherId);
}
