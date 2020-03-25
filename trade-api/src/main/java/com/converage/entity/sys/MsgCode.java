package com.converage.entity.sys;

import com.converage.architecture.mybatis.annotation.Column;
import com.converage.architecture.mybatis.annotation.Id;
import com.converage.architecture.mybatis.annotation.Table;
import lombok.Data;
import org.apache.ibatis.type.Alias;

import java.io.Serializable;
import java.sql.Timestamp;

@Data
@Alias("MsgCode")
@Table(name = "sys_msg_code")
public class MsgCode implements Serializable{
    private static final long serialVersionUID = 9118968377768240390L;

    @Id
    @Column(name = "id")
    private String id;

    //手机号
    @Column(name = "phone_number")
    private String phoneNumber;

    //
    @Column(name = "code_number")
    private String codeNumber;

    //创建人
    @Column(name = "create_time")
    private Timestamp createTime;
}
