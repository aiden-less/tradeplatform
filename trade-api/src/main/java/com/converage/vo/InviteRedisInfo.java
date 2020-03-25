package com.converage.vo;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Map;

public class InviteRedisInfo implements Serializable {

    private Integer inviteQuantity;

    private BigDecimal machineVolume;

    private Map<BigDecimal, Integer> machineMap;


    public Integer getInviteQuantity() {
        return inviteQuantity;
    }

    public void setInviteQuantity(Integer inviteQuantity) {
        this.inviteQuantity = inviteQuantity;
    }

    public BigDecimal getMachineVolume() {
        return machineVolume;
    }

    public void setMachineVolume(BigDecimal machineVolume) {
        this.machineVolume = machineVolume;
    }

    public Map<BigDecimal, Integer> getMachineMap() {
        return machineMap;
    }

    public void setMachineMap(Map<BigDecimal, Integer> machineMap) {
        this.machineMap = machineMap;
    }
}
