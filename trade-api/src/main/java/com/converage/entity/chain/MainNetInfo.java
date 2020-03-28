package com.converage.entity.chain;

import com.converage.architecture.mybatis.annotation.Column;
import com.converage.architecture.mybatis.annotation.Id;
import com.converage.architecture.mybatis.annotation.Table;
import lombok.Data;
import org.apache.ibatis.type.Alias;
import java.io.Serializable;
import java.math.BigDecimal;

@Data
@Alias("MainNetInfo")
@Table(name = "main_net_info")
public class MainNetInfo implements Serializable{
    private static final long serialVersionUID = -938520704642066269L;

    @Id
    @Column(name = Id)
    private String id;

    @Column(name = Net_name)
    private String netName; //

    @Column(name = Block_number)
    private Long blockNumber; //

    @Column(name = Block_sync_limit_number)
    private Long blockSyncLimitNumber; //

    @Column(name = Fee_amount)
    private BigDecimal feeAmount; //

    @Column(name = If_Valid)
    private Boolean ifValid; //


    //DB Column name
    public static final String Id = "id";
    public static final String Net_name = "net_name";
    public static final String Block_number = "block_number";
    public static final String Block_sync_limit_number = "block_sync_limit_number";
    public static final String Fee_amount = "fee_amount";
    public static final String If_Valid = "if_Valid";

}
