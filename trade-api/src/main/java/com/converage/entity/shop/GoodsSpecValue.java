package com.converage.entity.shop;

import com.converage.architecture.dto.Pagination;
import com.converage.architecture.mybatis.annotation.Column;
import com.converage.architecture.mybatis.annotation.Id;
import com.converage.architecture.mybatis.annotation.Table;
import lombok.Data;
import org.apache.ibatis.type.Alias;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

@Data
@Alias("GoodsSpecValue")
@Table(name = "goods_spec_value")//规格值表
public class GoodsSpecValue implements Serializable {

    private static final long serialVersionUID = 6675842731486652762L;

    @Id
    @Column(name = Id)
    private String id;

    @Column(name = Spec_id)
    private String specId; //规格表id（goods_spec_name表id）

    @Column(name = Spec_value)
    private String specValue;//规格值

    @Column(name = Create_time)
    private Timestamp createTime;

    @Column(name = Update_time)
    private Timestamp updateTime;

    //扩展属性
    private Pagination pagination;

    private String label;
    private List children = new ArrayList();

    //DB Column name
    public static final String Id = "id";
    public static final String Spec_id = "spec_id";
    public static final String Spec_value = "spec_value";
    public static final String Create_time = "create_time";
    public static final String Update_time = "update_time";

}
