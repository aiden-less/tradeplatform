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
@Alias("GoodsCategory")
@Table(name = "goods_category")//商品类目表
public class GoodsCategory implements Serializable{

    private static final long serialVersionUID = -3052331644957889414L;

    @Id
    @Column(name = Id)
    private String id;

    @Column(name = Category_name)
    private String categoryName;

    @Column(name = Create_time)
    private Timestamp createTime;

    @Column(name = Update_time)
    private Timestamp updateTime;

    @Column(name = If_valid)
    private Boolean ifValid;

    @Column(name = Sort)
    private Integer sort;


    //扩展属性
    private Pagination pagination;

    //DB Column name
    public static final String Id = "id";
    public static final String Category_name = "category_name";
    public static final String Create_time = "create_time";
    public static final String Update_time = "update_time";
    public static final String If_valid = "if_valid";
    public static final String Sort = "sort";
}
