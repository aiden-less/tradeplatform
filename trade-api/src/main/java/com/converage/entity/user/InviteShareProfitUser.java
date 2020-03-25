package com.converage.entity.user;

import lombok.Data;
import org.apache.ibatis.type.Alias;

import java.io.Serializable;
import java.math.BigDecimal;
import java.sql.Timestamp;

@Data
@Alias("InviteShareProfitUser")
public class InviteShareProfitUser implements Serializable {

    private static final long serialVersionUID = -1611424205029146285L;

    public String id;
    public String inviteId;
    public Timestamp registerTime;
    public BigDecimal computeReward;
}
