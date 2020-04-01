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




}
