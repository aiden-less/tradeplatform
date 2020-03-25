package com.converage.vo;

import lombok.Data;

@Data
public class PaymentMethod {

    private Integer rateType;

    private String machineDescription;

    private Boolean able;

    public PaymentMethod() {
    }

    public PaymentMethod(Integer rateType, String machineDescription, Boolean able) {
        this.rateType = rateType;
        this.machineDescription = machineDescription;
        this.able = able;
    }
}
