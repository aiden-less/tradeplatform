package com.converage.entity.chain;

import com.converage.architecture.mybatis.annotation.Column;
import com.converage.architecture.mybatis.annotation.Id;
import com.converage.architecture.mybatis.annotation.Table;
import lombok.Data;
import org.apache.ibatis.type.Alias;

import java.io.Serializable;

@Data
@Alias("MainNetUserAddr")
@Table(name = "main_net_user_addr")
public class MainNetUserAddr implements Serializable{

    private static final long serialVersionUID = 4870683107629279620L;
    @Id
    @Column(name = Id)
    private String id;

    @Column(name = Main_net_id)
    private String mainNetId; //

    @Column(name = User_id)
    private String userId; //

    @Column(name = Main_net_addr)
    private String mainNetAddr; //

    @Column(name = Private_key)
    private String privateKey; //

    //DB Column name
    public static final String Id = "id";
    public static final String Main_net_id = "main_net_id";
    public static final String User_id = "user_id";
    public static final String Main_net_addr = "main_net_addr";
    public static final String Private_key = "private_key";
}
