package com.converage.entity.currency.MatrixData;

import lombok.Data;

import java.io.Serializable;

@Data
public class MatrixDataResult implements Serializable{
    private static final long serialVersionUID = -2631817631817523623L;


    private String icon;
    private Long marketcap;
    private String symbol;
}
