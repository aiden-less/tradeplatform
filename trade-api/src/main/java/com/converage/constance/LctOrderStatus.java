package com.converage.constance;

/**
 * Created by 旺旺 on 2020/4/9.
 */
public enum LctOrderStatus {
    UN_FINISH(1), FINISH(2), CANCEL(3), APPEAL(4);

    private int status;

    LctOrderStatus(int status) {
        this.status = status;
    }

    public int getStatus() {
        return this.status;
    }
}
