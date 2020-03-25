package com.converage.entity.currency.MatrixData;

import lombok.Data;

import java.util.List;

@Data
public class MatrixDataRsp {
    private MatrixDataHead Head;
    private List<MatrixDataResult> Result;
}
