package com.converage.mapper.user;

import com.converage.entity.user.UserMerchantRecord;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserMerchantMapper {

    List<UserMerchantRecord> listExpireMerchant(@Param("expireTime") String expireTime);

    void updateMerchantRecord(@Param("ids") List<String> userMerchantIds);

    void updateUserUserType(@Param("ids") List<String> userIds, @Param("userType") Integer userType);
}
