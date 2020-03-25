package com.converage.entity.work;

import com.converage.architecture.mybatis.annotation.Column;
import com.converage.architecture.mybatis.annotation.Id;
import lombok.Data;
import org.apache.ibatis.type.Alias;

import java.io.Serializable;
import java.sql.Timestamp;

@Data
@Alias("Project")
//@Table(name = "work_project")
public class Project implements Serializable{
    private static final long serialVersionUID = -6618894313915973579L;

    @Id
    @Column(name = "id")
    private String id;

    @Column(name = "project_name")
    private String projectName;

    @Column(name = "remark")
    private String remark;

    @Column(name = "create_by")
    private String createBy;

    @Column(name = "create_time")
    private Timestamp createTime;

    @Column(name = "if_valid")
    private Boolean ifValid;
}
