package com.converage.entity.shop;

import com.converage.architecture.mybatis.annotation.Column;
import com.converage.architecture.mybatis.annotation.Id;
import com.converage.architecture.mybatis.annotation.Table;
import lombok.Data;
import org.apache.ibatis.type.Alias;

import java.io.Serializable;

@Data
@Alias("Merchant")
@Table(name = "shop_merchant")//订单基本信息表
public class Merchant implements Serializable {
    private static final long serialVersionUID = -8842878851819966872L;

    @Id
    @Column(name = Id)
    private String id;

    @Column(name = Title)
    private String title;

    @Column(name = City_code)
    private String cityCode;

    @Column(name = Address)
    private String address;

    @Column(name = ImgUrl)
    private String imgUrl;

    @Column(name = BussinessTime)
    private String bussinessTime;

    @Column(name = Longitude)
    private Double longitude;

    @Column(name = Latitude)
    private Double latitude;

    private Integer distance;

    //DB Column name
    public static final String Id = "id";
    public static final String Title = "title";
    public static final String City_code = "city_code";
    public static final String Address = "address";
    public static final String ImgUrl = "img_url";
    public static final String BussinessTime = "bussiness_time";
    public static final String Longitude = "longitude";
    public static final String Latitude = "latitude";
}
