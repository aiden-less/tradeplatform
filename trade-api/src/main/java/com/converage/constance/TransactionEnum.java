package com.converage.constance;

/**
 * Created by 旺旺 on 2020/3/17.
 */
public enum TransactionEnum {
    BUY(1), SELL(2),

    UN_FINISH(0), FINISH(1), CANCEL(2);

    private Integer type;


    TransactionEnum(Integer type) {
        this.type = type;
    }

    public Integer getType() {
        return type;
    }


}
