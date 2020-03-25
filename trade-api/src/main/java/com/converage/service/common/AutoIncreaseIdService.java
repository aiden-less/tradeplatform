package com.converage.service.common;

import com.converage.entity.common.UserInviteCodeId;
import com.converage.mapper.common.AutoIncreaseIdMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AutoIncreaseIdService {

    @Autowired
    private AutoIncreaseIdMapper autoIncreaseIdMapper;

    private Integer createUserInviteCodeId(UserInviteCodeId userInviteCodeId){
        return autoIncreaseIdMapper.insertUserInviteCode(userInviteCodeId);
    }
}
