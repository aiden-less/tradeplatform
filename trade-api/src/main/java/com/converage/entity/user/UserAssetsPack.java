package com.converage.entity.user;

import com.converage.architecture.dto.Pagination;
import com.converage.architecture.mybatis.annotation.Column;
import com.converage.architecture.mybatis.annotation.Id;
import com.converage.architecture.mybatis.annotation.Table;
import lombok.Data;
import org.apache.ibatis.type.Alias;

import java.io.Serializable;
import java.sql.Timestamp;

@Data
@Alias("UserAssetsPack")
@Table(name = "user_assets_pack") //用戶资产包
public class UserAssetsPack implements Serializable {
    private static final long serialVersionUID = -2201906745647194493L;

    @Id
    @Column(name = Id)
    private String id;

    @Column(name = User_id)
    private String userId; //

    @Column(name = Assets_pack_id)
    private String assetsPackId; //

    @Column(name = User_name)
    private String userName; //

    @Column(name = Pack_name)
    private String packName; //

    @Column(name = Introduction)
    private String introduction; //

    @Column(name = Remark)
    private String remark; //

    @Column(name = Home_img)
    private String homeImg; //

    @Column(name = Create_time)
    private Timestamp createTime; //

    @Column(name = If_valid)
    private Boolean ifValid; //

    @Column(name = Logistic_number)
    private String logisticNumber; //物流编码

    @Column(name = If_send)
    private Boolean ifSend; //

    @Column(name = Send_time)
    private Timestamp sendTime; //

    public static final String Id = "id";
    public static final String User_id = "user_id";
    public static final String Assets_pack_id = "assets_pack_id";
    public static final String User_name = "user_name";
    public static final String Pack_name = "pack_name";
    public static final String Introduction = "introduction";
    public static final String Remark = "remark";
    public static final String Home_img = "home_img";
    public static final String Create_time = "create_time";
    public static final String If_valid = "if_valid";
    public static final String If_send = "if_send";
    public static final String Logistic_number = "logistic_number";
    public static final String Send_time = "send_time";

    private Pagination pagination;
}
