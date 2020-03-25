package com.converage.entity.shop;

import com.converage.architecture.dto.Pagination;
import com.converage.architecture.mybatis.annotation.Column;
import com.converage.architecture.mybatis.annotation.Id;
import com.converage.architecture.mybatis.annotation.Table;
import lombok.Data;
import org.apache.ibatis.type.Alias;

import java.io.Serializable;
import java.sql.Timestamp;

@Data
@Alias("GoodsBrand")
@Table(name = "goods_brand")//商品品牌表
public class GoodsBrand implements Serializable{
    private static final long serialVersionUID = -6707589402324367572L;

    @Id
    @Column(name = "id")
    private String id;

    @Column(name = Brand_name)
    private String brandName;

    @Column(name = Subscriber_id)
    private String subscriberId;

    @Column(name = Create_time)
    private Timestamp createTime;

    @Column(name = Update_time)
    private Timestamp updateTime;

    @Column(name = If_valid)
    private Boolean ifValid;

    //扩展属性
    private Pagination pagination;

    //DB Column name
    public static final String Id = "id";
    public static final String Brand_name = "brand_name";
    public static final String Create_time = "create_time";
    public static final String Update_time = "update_time";
    public static final String Subscriber_id = "subscriber_id";
    public static final String If_valid = "if_valid";


}
