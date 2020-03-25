package com.converage.mapper.common;

import com.converage.entity.common.UserInviteCodeId;
import org.springframework.stereotype.Repository;

@Repository
public interface AutoIncreaseIdMapper {
    Integer insertUserInviteCode(UserInviteCodeId userInviteCodeId);
}
