package com.converage.entity.user;

import com.converage.architecture.mybatis.annotation.Column;
import com.converage.architecture.mybatis.annotation.Id;
import com.converage.architecture.mybatis.annotation.Table;
import lombok.Data;
import org.apache.ibatis.type.Alias;

import java.io.Serializable;
import java.sql.Timestamp;

@Data
@Alias("BankInfo")
@Table(name = "bank_info")
public class BankInfo  implements Serializable {
    @Id
    @Column(name = Id)
    private String id;

    @Column(name = Name)
    private String name;

    @Column(name = Icon)
    private String icon;

    @Column(name = Create_time)
    private Timestamp createTime;


    //DB Column name
    public static final String Id = "id";
    public static final String Name = "name";
    public static final String Icon = "icon";
    public static final String Create_time = "create_time";
}