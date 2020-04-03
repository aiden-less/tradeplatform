package com.converage.service.assets;

import com.converage.dto.AssetsFinanceQuery;
import com.converage.entity.assets.LctAssets;
import com.converage.mapper.user.LctAssetsMapper;
import com.converage.architecture.service.BaseService;
import com.converage.entity.assets.CctAssets;
import com.converage.mapper.user.CctAssetsMapper;
import com.converage.utils.ValueCheckUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class UserAssetsService extends BaseService {

    @Autowired
    private CctAssetsMapper cctAssetsMapper;

    @Autowired
    private LctAssetsMapper lctAssetsMapper;

    //币币账号资产列表
    public List<CctAssets> cctAssetsList(String userId) {
        return cctAssetsMapper.listUserAssets(userId);
    }

    //币币账号资产详情
    public CctAssets cctAssetsDetail(AssetsFinanceQuery AssetsFinanceQuery) {
        String coinId = AssetsFinanceQuery.getCoinId();
        ValueCheckUtils.notEmptyString(coinId, "请选择币种");
        CctAssets cctAssets = cctAssetsMapper.getUserAssets(AssetsFinanceQuery);
        cctAssets.setFinanceLogs(cctAssetsMapper.listFinanceLog(AssetsFinanceQuery));
        return cctAssets;
    }

    //法币账号资产列表
    public List<LctAssets> lctAssetsList(String userId) {
        return lctAssetsMapper.listUserAssets(userId);
    }

    //法币账号资产详情
    public LctAssets LctAssetsDetail(AssetsFinanceQuery AssetsFinanceQuery) {
        String coinId = AssetsFinanceQuery.getCoinId();
        ValueCheckUtils.notEmptyString(coinId, "请选择币种");
        LctAssets lctAssets = lctAssetsMapper.getUserAssets(AssetsFinanceQuery);
        lctAssets.setFinanceLogs(lctAssetsMapper.listFinanceLog(AssetsFinanceQuery));
        return lctAssets;
    }

}
