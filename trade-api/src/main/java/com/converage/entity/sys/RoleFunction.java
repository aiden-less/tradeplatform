package com.converage.entity.sys;

import com.converage.architecture.mybatis.annotation.Column;
import com.converage.architecture.mybatis.annotation.Id;
import com.converage.architecture.mybatis.annotation.Table;
import lombok.Data;
import org.apache.ibatis.type.Alias;

import java.io.Serializable;

@Data
@Alias("RoleFunction")
@Table(name = "sys_role_function")
public class RoleFunction implements Serializable {
    private static final long serialVersionUID = 6683333238467964092L;

    @Id
    @Column(name = "id")
    private String id;

    @Column(name = "function_id")
    private String functionId;

    @Column(name = "role_id")
    private String roleId;


    public RoleFunction(String functionId, String roleId) {
        this.functionId = functionId;
        this.roleId = roleId;
    }
}
