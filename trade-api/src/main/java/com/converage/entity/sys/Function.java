package com.converage.entity.sys;

import com.converage.architecture.mybatis.annotation.Column;
import com.converage.architecture.mybatis.annotation.Id;
import com.converage.architecture.mybatis.annotation.Table;
import lombok.Data;
import org.apache.ibatis.type.Alias;

import java.io.Serializable;
import java.sql.Timestamp;

@Data
@Alias("Function")
@Table(name = "sys_function")
public class Function implements Serializable{
    private static final long serialVersionUID = 92968053084215365L;

    //database column
    //tId
    @Id
    @Column(name = "id")
    private String id;
    //父id
    @Column(name = "parent_id")
    private String parentId;

    //接口名
    @Column(name = "function_name")
    private String functionName;

    //vue组件所属模块
    @Column(name = "module_name")
    private String moduleName;

    //url
    @Column(name = "url")
    private String url;

    //是否属于节点选项
    @Column(name = "is_leaf")
    private Boolean isLeaf;

    //对应vue组件名
    @Column(name = "component")
    private String component = "";

    //图标代码
    @Column(name = "icon_code")
    private String iconCode;

    //创建人
    @Column(name = "create_by")
    private String createBy = "system";

    //创建时间
    @Column(name = "create_time")
    private Timestamp createTime = new Timestamp(System.currentTimeMillis());

    //是否有效
    @Column(name = "if_valid")
    private Boolean ifValid = true;



}
