package com.converage.entity.sys;

import lombok.Data;
import org.apache.ibatis.type.Alias;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Data
@Alias("FuncTreeNode")
public class FuncTreeNode implements Serializable {
    private static final long serialVersionUID = 6996686865437690511L;
    private String id;
    private String pid;
    private String name;
    private String label;
    private String component;
    private String module;
    private String iconCode;
    private String url;
    private String isLeaf;
    private List<FuncTreeNode> children = new ArrayList<>();
}
