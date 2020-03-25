package com.converage.controller.admin.req;

import lombok.Data;

import java.io.Serializable;

@Data
public class GoodsSkuReq implements Serializable{
    private static final long serialVersionUID = -1981205737684074925L;

    //spuId
    public String spuId;
    //规格名规格值字符串 格式:['1':'2','1','2']
    public String specNameValueStr;
}
