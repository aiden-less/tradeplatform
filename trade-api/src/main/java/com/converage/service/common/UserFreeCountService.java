package com.converage.service.common;

import com.converage.architecture.service.BaseService;
import com.converage.entity.common.UserFreeCount;
import com.converage.mapper.user.UserFreeCountMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;

@Service
public class UserFreeCountService extends BaseService {

    @Autowired
    private UserFreeCountMapper userFreeCountMapper;

    @Autowired
    private GlobalConfigService globalConfigService;



    public void initFreeCount() {
        List<String> updateField = Arrays.asList(UserFreeCount.Share_count);
        userFreeCountMapper.initMaxCount(updateField, 0);
    }

}
