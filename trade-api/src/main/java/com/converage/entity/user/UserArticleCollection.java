package com.converage.entity.user;


import com.converage.architecture.mybatis.annotation.Column;
import com.converage.architecture.mybatis.annotation.Id;
import com.converage.architecture.mybatis.annotation.Table;
import lombok.Data;
import org.apache.ibatis.type.Alias;

import java.io.Serializable;
import java.sql.Timestamp;

@Data
@Alias("UserArticleCollection")
@Table(name = "user_article_collection")
public class UserArticleCollection implements Serializable {
    private static final long serialVersionUID = -3481512245256625366L;

    @Id
    @Column(name = Id)
    private String id;

    @Column(name = User_id)
    private String userId;

    @Column(name = Article_id)
    private String articleId;

    @Column(name = Article_title)
    private String articleTitle;

    @Column(name = Article_type)
    private Integer articleType;

    @Column(name = Create_time)
    private Timestamp createTime;

    //DB Column name
    public static final String Id = "id";
    public static final String User_id = "user_id";
    public static final String Article_id = "article_id";
    public static final String Article_title = "article_title";
    public static final String Article_type = "article_type";
    public static final String Create_time = "create_time";

    //扩展属性
    private Boolean ifLike; //是否点赞
    private Long likeAmount; //点赞数
    private Boolean ifCollect; //是否收藏

    public UserArticleCollection(){}


    public UserArticleCollection(String userId, String articleId, String articleTitle, Integer articleType) {
        this.userId = userId;
        this.articleId = articleId;
        this.articleTitle = articleTitle;
        this.articleType = articleType;
    }
}
