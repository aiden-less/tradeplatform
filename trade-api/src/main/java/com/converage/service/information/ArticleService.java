package com.converage.service.information;

import com.google.common.collect.ImmutableMap;
import com.converage.architecture.dto.Pagination;
import com.converage.architecture.exception.BusinessException;
import com.converage.architecture.service.BaseService;
import com.converage.client.RedisClient;
import com.converage.constance.CommonConst;
import com.converage.constance.InformationConst;
import com.converage.constance.RedisKeyConst;
import com.converage.entity.information.Article;
import com.converage.entity.shop.GoodsSpu;
import com.converage.entity.sys.Subscriber;
import com.converage.entity.user.UserArticleCollection;
import com.converage.mapper.information.ArticleMapper;
import com.converage.service.common.GlobalConfigService;
import com.converage.utils.ValueCheckUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallbackWithoutResult;
import org.springframework.transaction.support.TransactionTemplate;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

@Service
public class ArticleService extends BaseService {

    @Autowired
    private TransactionTemplate transactionTemplate;

    @Autowired
    private GlobalConfigService globalConfigService;

    @Autowired
    private RedisClient redisClient;

    @Autowired
    private ArticleMapper articleMapper;


    /**
     * 发布文章
     *
     * @param userId
     * @param article
     */
    public void createArticle(String userId, Article article) {
        if (true) {
            Subscriber subscriber = selectOneById(userId, Subscriber.class);
            ValueCheckUtils.notEmpty(subscriber, "非管理员用户不能发布公告和活动");
        }
        article.setCreateBy(userId);
        Integer i = insertIfNotNull(article);
        if (i == 0) {
            throw new BusinessException("发布文章失败");
        }
    }


    /**
     * 所有资讯列表
     *
     * @return
     */
    public Map<String, Object> allArticle() {
        Pagination<Article> pagination1 = new Pagination<>(0, 10);
        Article article = new Article();
        article.setArticleType(InformationConst.ARTICLE_TYPE_NOTICE);
        article.setIfValid(true);
        pagination1.setParam(article);
        List<Article> notice = articleMapper.selectByPage(pagination1);
        article.setArticleType(InformationConst.ARTICLE_TYPE_INDUSTRY);
        List<Article> industry = articleMapper.selectByPage(pagination1);

        Map<String, Object> whereMap3 = ImmutableMap.of(Article.Article_type + "=", InformationConst.ARTICLE_TYPE_ACTIVITY, Article.IfValid + "=", true);
        List<String> fields = Arrays.asList(Article.Id, Article.Article_type, Article.Article_title, Article.Article_cover, Article.CreateTime);
        List<Article> activity = selectiveListByWhereMap(fields, whereMap3, Article.class);
//
//        String blockChainLink = globalConfigService.getByDb(GlobalConfigService.Enum.BLOCK_CHAIN_LINK);

        return ImmutableMap.of(
                "notice", notice,
                "industry", industry,
                "activity", activity
//                "blockChainLink", blockChainLink
        );
    }

    public Map<String, Object> homeArticle() {
        Pagination<Article> pagination = new Pagination<>(0, 10);
        Map<String, Object> orderMap = ImmutableMap.of(Article.CreateTime, CommonConst.MYSQL_DESC);

        Map<String, Object> whereMap1 = ImmutableMap.of(Article.Article_type + "=", InformationConst.ARTICLE_TYPE_NOTICE, Article.IfValid + "=", true);
        List<Article> notice = selectListByWhereMap(whereMap1, pagination, Article.class, orderMap);

        Map<String, Object> whereMap2 = ImmutableMap.of(Article.Article_type + "=", InformationConst.ARTICLE_TYPE_ADVERTISE, Article.IfValid + "=", true);
        List<Article> advertise = selectListByWhereMap(whereMap2, Article.class, orderMap);

        return ImmutableMap.of(
                "notice", notice,
                "advertise", advertise
        );
    }

    /**
     * 根据类型查询文章
     *
     * @param pagination
     * @param articleType InformationConst.ARTICLE_TYPE_*
     * @return
     */
    public List<Article> listArticle(Integer articleType, Pagination<Article> pagination) {
        Article article = new Article();
        article.setArticleType(articleType);
        article.setIfValid(true);
        pagination.setParam(article);
        return articleMapper.selectByPage(pagination);
    }

    /**
     * 用户文章收藏列表
     *
     * @param userId
     * @param pagination
     * @return
     */
    public List<UserArticleCollection> listArticleCollection(String userId, Pagination pagination) {
        Map<String, Object> whereMap = ImmutableMap.of(UserArticleCollection.User_id + "=", userId);
        List<UserArticleCollection> collections = selectListByWhereMap(whereMap, pagination, UserArticleCollection.class);
        for (UserArticleCollection userArticleCollection : collections) {
            String articleId = userArticleCollection.getArticleId();
            String likeKey = String.format(RedisKeyConst.ARTICLE_LIKE, articleId);
            userArticleCollection.setIfLike(redisClient.isMember(likeKey, userId));//是否点赞
            userArticleCollection.setLikeAmount((redisClient.setSize(likeKey)));//点赞数
            userArticleCollection.setIfCollect(true); //是否收藏
        }
        return collections;
    }

    /**
     * 根据id查询文章
     *
     * @param articleId
     * @return
     */
    public Article getArticleById(String articleId) {
        Article article = selectOneById(articleId, Article.class);
        if (article != null && Boolean.TRUE.equals(article.getIfValid())) {
            return article;
        }
        return null;
    }

    /**
     * 收藏文章/取消收藏文章
     *
     * @param articleId 文章id
     * @param userId    用户id
     */
    public void updateCollection(String userId, String articleId) {
        Article article = selectOneById(articleId, Article.class);
        ValueCheckUtils.notEmpty(article, "未找到文章");
        String key = String.format(RedisKeyConst.ARTICLE_COLLECT, articleId);
        boolean member = redisClient.isMember(key, userId);
        if (member) {
            redisClient.remove(key, userId);
        } else {
            redisClient.add(key, userId);
        }
    }

    /**
     * 点赞文章/取消点赞文章
     *
     * @param userId    用户id
     * @param articleId 文章id
     */
    public void updateLike(String userId, String articleId) {
        Article article = selectOneById(articleId, Article.class);
        ValueCheckUtils.notEmpty(article, "未找到文章");
        String key = String.format(RedisKeyConst.ARTICLE_LIKE, articleId);
        boolean member = redisClient.isMember(key, userId);
        if (member) {
            redisClient.remove(key, userId);
        } else {
            redisClient.add(key, userId);
        }
    }

    /**
     * 审核文章
     *
     * @param articleId
     * @param vertifyStatus
     */
    public void updateArticleVertify(String articleId, Integer vertifyStatus) {
        transactionTemplate.execute(new TransactionCallbackWithoutResult() {
            @Override
            protected void doInTransactionWithoutResult(TransactionStatus transactionStatus) {
                String userId = selectOneById(articleId, Article.class).getCreateBy();

                Article article = new Article();
                article.setId(articleId);
                article.setVerifyStatus(vertifyStatus);
                Integer i = updateIfNotNull(article);
                if (i == 0) {
                    throw new BusinessException("审核失败");
                }
            }
        });
    }


    public List<Article> getByPage(Pagination<Article> pagination) {
        List<Article> articles = articleMapper.selectByPage(pagination);
        for (Article article : articles) {
            if (article.getContentType() == InformationConst.CONTENT_TYPE_GOODS) {
                article.setGoodsName(selectOneById(article.getArticleContent(), GoodsSpu.class).getGoodsName());
            }
        }
        return articles;
    }


}
