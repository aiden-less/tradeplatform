package com.converage.entity.pay;

import com.converage.architecture.mybatis.annotation.Column;
import com.converage.architecture.mybatis.annotation.Id;
import com.converage.architecture.mybatis.annotation.Table;
import lombok.Data;
import org.apache.ibatis.type.Alias;
import java.io.Serializable;
import java.sql.Timestamp;

@Data
@Alias("PayInfo")
@Table(name = "pay_info")
public class PayInfo implements Serializable {
    private static final long serialVersionUID = -22762641073723501L;
    @Id
    @Column(name = Id)
    private String id;

    @Column(name = User_id)
    private String userId;

    @Column(name = Real_name)
    private String realName;

    @Column(name = Bank_name)
    private String bankName;

    @Column(name = Bank_address)
    private String bankAddress;

    @Column(name = Account)
    private String account;

    @Column(name = Qrcode)
    private String qrcode;

    @Column(name = Create_time)
    private Timestamp createTime;

    //DB Column name
    public static final String Id = "id";
    public static final String User_id = "user_id";
    public static final String Real_name = "real_name";
    public static final String Bank_name = "bank_name";
    public static final String Bank_address = "bank_address";
    public static final String Account = "account";
    public static final String Qrcode = "qrcode";
    public static final String Create_time = "create_time";
}