package com.converage.dto;

import lombok.Data;

/**
 * Created by 旺旺 on 2020/4/3.
 */
@Data
public class AssetsFinanceQuery {
    private int pageNum = 0;
    private String userId;
    private String coinId;
    private int type = 0;
    private int status;
    private int index = pageNum * 20;
}
