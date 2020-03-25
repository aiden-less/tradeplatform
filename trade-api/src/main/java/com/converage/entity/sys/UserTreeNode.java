package com.converage.entity.sys;

import lombok.Data;
import org.apache.ibatis.type.Alias;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Data
@Alias("UserTreeNode")
public class UserTreeNode implements Serializable{
    private static final long serialVersionUID = 7934130613893115336L;

    private String id;
    private String inviteId;
    private String label;
    private List<UserTreeNode> children = new ArrayList<>();
}
