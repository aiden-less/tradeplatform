package com.converage.entity.work;

import com.converage.architecture.mybatis.annotation.Column;
import com.converage.architecture.mybatis.annotation.Id;
import lombok.Data;
import org.apache.ibatis.type.Alias;

import java.io.Serializable;

@Data
@Alias("ParamStatement")
//@Table(name = "work_param_statement")
public class ParamStatement implements Serializable {
    private static final long serialVersionUID = -2170145517447409836L;

    @Id
    @Column(name = "id")
    private String id;

    @Column(name = "appInterface_id")
    private String appInterfaceId;

    @Column(name = "data_key")
    private String dataKey;

    @Column(name = "data_type")
    private String dataType;

    @Column(name = "remark")
    private String remark;

    @Column(name = "req_rsp_type")
    private String reqRspType;
}
