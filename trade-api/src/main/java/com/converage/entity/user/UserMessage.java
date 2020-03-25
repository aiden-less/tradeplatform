package com.converage.entity.user;

import com.converage.architecture.dto.Pagination;
import com.converage.architecture.mybatis.annotation.Column;
import com.converage.architecture.mybatis.annotation.Id;
import com.converage.architecture.mybatis.annotation.Table;
import lombok.Data;
import org.apache.ibatis.type.Alias;

import java.io.Serializable;
import java.sql.Timestamp;

@Data
@Alias("UserMessage")
@Table(name = "user_message")
public class UserMessage implements Serializable {
    private static final long serialVersionUID = 3478293500231433307L;

    @Id
    @Column(name = Id)
    private String id;

    @Column(name = User_id)
    private String userId;

    @Column(name = Title)
    private String title;

    @Column(name = Type)
    private Integer type;

    @Column(name = Content)
    private String content;

    @Column(name = Extra_content)
    private String extraContent;

    @Column(name = Img_url)
    private String imgUrl;

    @Column(name = Create_time)
    private Timestamp createTime;

    @Column(name = If_read)
    private Boolean ifRead;

    @Column(name = If_valid)
    private Boolean ifValid;


    private String userName;
    private Pagination pagination;
    private Integer countNotRead;

    private String defaultImgUrl;

    public static final String Id = "id";
    public static final String User_id = "user_id";
    public static final String Title = "title";
    public static final String Type = "type";
    public static final String Content = "content";
    public static final String Extra_content = "extra_content";
    public static final String Img_url = "img_url";
    public static final String Create_time = "create_time";
    public static final String If_read = "if_read";
    public static final String If_valid = "if_valid";

    public UserMessage(){

    }

    public UserMessage(String userId, String title, Integer type, String content, String extraContent, String imgUrl) {
        this.userId = userId;
        this.title = title;
        this.type = type;
        this.content = content;
        this.extraContent = extraContent;
        this.imgUrl = imgUrl;

    }
}
