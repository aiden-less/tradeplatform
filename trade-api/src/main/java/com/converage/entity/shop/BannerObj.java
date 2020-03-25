package com.converage.entity.shop;


import com.converage.architecture.mybatis.annotation.Column;
import com.converage.architecture.mybatis.annotation.Id;
import lombok.Data;
import org.apache.ibatis.type.Alias;

@Data
@Alias("BannerObj")
//@Table(name = "shop_banner_obj")//商城banner图
public class BannerObj {

    @Id
    @Column(name = "obj_id")
    private String objId;

    @Column(name = "obj_type")
    private Integer objType;

    @Column(name = "img_url")
    private String imgUrl;
}
