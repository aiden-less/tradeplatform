package com.converage.entity.sys;

import com.converage.architecture.mybatis.annotation.Column;
import com.converage.architecture.mybatis.annotation.Id;
import com.converage.architecture.mybatis.annotation.Table;
import lombok.Data;
import org.apache.ibatis.type.Alias;

import java.io.Serializable;

@Data
@Alias("SubscriberRole")
@Table(name = "sys_subscriber_role")
public class SubscriberRole implements Serializable {
    private static final long serialVersionUID = -3735896857992515934L;

    @Id
    @Column(name = "id")
    private String id;

    @Column(name = "subscriber_id")
    private String subscriberId;

    @Column(name = "role_id")
    private String roleId;

    public SubscriberRole(String subscriberId, String roleId) {
        this.subscriberId = subscriberId;
        this.roleId = roleId;
    }
}
