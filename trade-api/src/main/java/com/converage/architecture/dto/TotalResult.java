package com.converage.architecture.dto;

import java.util.List;

public class TotalResult<T> {
    private T total;

    private List<T> list;

    public TotalResult(T total, List<T> list) {
        this.total = total;
        this.list = list;
    }
    public T getTotal() {
        return total;
    }

    public void setTotal(T total) {
        this.total = total;
    }

    public List<T> getList() {
        return list;
    }

    public void setList(List<T> list) {
        this.list = list;
    }

}
