package com.converage.entity.assets;


import com.converage.architecture.dto.Pagination;
import com.converage.architecture.mybatis.annotation.Column;
import com.converage.architecture.mybatis.annotation.Id;
import com.converage.architecture.mybatis.annotation.Table;
import lombok.Data;
import org.apache.ibatis.type.Alias;

import java.io.Serializable;
import java.math.BigDecimal;
import java.sql.Timestamp;

@Data
@Alias("WalletTransferRecord")
@Table(name = "wallet_transfer_record")
public class WalletTransferRecord implements Serializable {
    private static final long serialVersionUID = 6353340436172485758L;

    @Id
    @Column(name = Id)
    private String id;

    @Column(name = Settlement_name)
    private String settlementName;

    @Column(name = Transfer_type)
    private String transferType;

    @Column(name = From_address)
    private String fromAddress;

    @Column(name = To_address)
    private String toAddress;

    @Column(name = Transfer_amount)
    private BigDecimal transferAmount;

    @Column(name = Transaction_hash)
    private String transactionHash;

    @Column(name = Create_time)
    private Timestamp createTime;


    private Pagination pagination;

    public WalletTransferRecord() {
    }

    public WalletTransferRecord(String settlementName, String transferType, String fromAddress, String toAddress, BigDecimal transferAmount, Timestamp timestamp, String transactionHash) {
        this.settlementName = settlementName;
        this.transferType = transferType;
        this.fromAddress = fromAddress;
        this.toAddress = toAddress;
        this.transferAmount = transferAmount;
        this.transactionHash = transactionHash;
        this.createTime = timestamp;
    }

    //DB Column name
    public static final String Id = "id";
    public static final String Settlement_name = "settlement_name";
    public static final String Transfer_type = "transfer_type";
    public static final String From_address = "from_address";
    public static final String To_address = "to_address";
    public static final String Transfer_amount = "transfer_amount";
    public static final String Transaction_hash = "transaction_hash";
    public static final String Create_time = "create_time";

}
