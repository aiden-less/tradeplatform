package com.converage.entity.work;

import lombok.Data;
import org.apache.ibatis.type.Alias;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Data
@Alias("ApiTreeNode")
public class ApiTreeNode implements Serializable{
    private static final long serialVersionUID = -8540398946068313293L;
    private String id;
    private String pid;
    private String label;
    private Boolean isProjectRoot;
    private List<ApiTreeNode> children = new ArrayList<>();
}
