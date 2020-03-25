package com.converage.entity.user;

import com.converage.architecture.mybatis.annotation.Column;
import com.converage.architecture.mybatis.annotation.Id;
import com.converage.architecture.mybatis.annotation.Table;
import lombok.Data;
import org.apache.ibatis.type.Alias;

import java.io.Serializable;

@Data
@Alias("UserAdvertisement")
@Table(name = "user_advertisement")
public class UserAdvertisement implements Serializable {
    private static final long serialVersionUID = 650932507503371572L;

    @Id
    @Column(name = Id)
    private String id;

    @Column(name = Img_url)
    private String imgUrl;

    @Column(name = Link_address)
    private String linkAddress;

    //DB Column name
    public static final String Id = "id";
    public static final String Img_url = "img_url";
    public static final String Link_address = "link_address";
}
