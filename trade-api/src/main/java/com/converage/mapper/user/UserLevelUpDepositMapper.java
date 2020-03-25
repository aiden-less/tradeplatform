package com.converage.mapper.user;

/**
 * 用户升级押金
 */
public interface UserLevelUpDepositMapper {

    Double selectTotalByUserId(String userId);

}