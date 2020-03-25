package com.converage.entity.user;


import com.converage.architecture.mybatis.annotation.Column;
import com.converage.architecture.mybatis.annotation.Id;
import com.converage.architecture.mybatis.annotation.Table;
import lombok.Data;
import org.apache.ibatis.type.Alias;

import java.io.Serializable;
import java.math.BigDecimal;

@Data
@Alias("UserNode")
@Table(name = "user_node")
public class UserNode implements Serializable{
    private static final long serialVersionUID = 2393254011495616288L;

    @Id
    @Column(name = Id)
    private String id;

    @Column(name = Team_level)
    private Integer teamLevel;

    @Column(name = Node_name)
    private String nodeName;

    @Column(name = Package_num)
    private Integer packageNum;

    @Column(name = Social_achievement)
    private BigDecimal socialAchievement;

    @Column(name = Reward_percentage)
    private BigDecimal rewardPercentage;

    private Integer operatorType;

    public static final String Id = "id";
    public static final String Team_level = "team_level";
    public static final String Node_name = "node_name";
    public static final String Package_num = "package_num";
    public static final String Social_achievement = "social_achievement";
    public static final String Reward_percentage = "reward_percentage";
}
