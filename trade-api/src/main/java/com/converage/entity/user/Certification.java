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
@Alias("Certification")
@Table(name = "user_certification")
public class Certification implements Serializable { //用户认证
    private static final long serialVersionUID = -3096477540596144940L;

    @Id
    @Column(name = Id)
    private String id;

    @Column(name = User_id)
    private String userId; //用户id

    @Column(name = Real_name)
    private String realName; //真实姓名

    @Column(name = License_type)
    private Integer licenseType; //证件类型

    @Column(name = License_number)
    private String licenseNumber; //证件号码

    @Column(name = Positive_photo_url)
    private String positivePhotoUrl; //证件正面照片

    @Column(name = Reverse_photo_url)
    private String reversePhotoUrl; //证件反面照片

    @Column(name = Handle_photo_url)
    private String handlePhotoUrl; //手持身份证照片

    @Column(name = Create_time)
    private Timestamp createTime; //申请认证时间

    //UserConst.USER_CERT_STATUS_*
    @Column(name = Status)
    private Integer status; //认证状态

    @Column(name = Fail_reason)
    private String failReason;

    //扩展属性
    private String userAccount;
    private String phoneNumber;
    private Pagination pagination;



    //DB Column name
    public static final String Id = "id";
    public static final String User_id = "user_id";
    public static final String Real_name = "real_name";
    public static final String License_type = "license_type";
    public static final String License_number = "license_number";
    public static final String Positive_photo_url = "positive_photo_url";
    public static final String Reverse_photo_url = "reverse_photo_url";
    public static final String Handle_photo_url = "handle_photo_url";
    public static final String Create_time = "create_time";
    public static final String Status = "status";
    public static final String Fail_reason = "fail_reason";

    public Certification(){}
}
