package com.converage.service.common;

import com.google.common.collect.ImmutableMap;
import com.converage.architecture.service.BaseService;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class ServerService extends BaseService{

    public Map<String,Object> serverInfo(){
        return ImmutableMap.of("currentTimes",System.currentTimeMillis());
    }
}
