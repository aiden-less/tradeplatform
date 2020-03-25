package com.converage.entity.user;

import com.converage.entity.assets.CctAssets;
import lombok.Data;

import java.util.List;

@Data
public class UserComputePowerRank {
    private String userName;
    private Integer computeRankNo;
    private List<CctAssets> cctAssetsList;
}
