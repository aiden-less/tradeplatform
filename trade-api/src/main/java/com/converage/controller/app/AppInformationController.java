package com.converage.controller.app;

import com.converage.architecture.dto.Pagination;
import com.converage.architecture.dto.Result;
import com.converage.architecture.utils.JwtUtils;
import com.converage.architecture.utils.ResultUtils;
import com.converage.entity.information.Article;
import com.converage.entity.information.Investigation;
import com.converage.service.information.ArticleService;
import com.converage.service.information.InvestigationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("app/information")
public class AppInformationController {

    @Autowired
    private ArticleService articleService;

    @Autowired
    private InvestigationService investigationService;



    /**
     * 查询所有文章
     */
    @RequestMapping("article/allList")
    public Result<?> allArticle(Pagination pagination, HttpServletRequest request) {
        return ResultUtils.success(articleService.allArticle());
    }

    /**
     * 主页资讯
     */
    @RequestMapping("article/home")
    public Result<?> homeArticle(Pagination pagination, HttpServletRequest request) {
        return ResultUtils.success(articleService.homeArticle());
    }

    /**
     * 按类型查询文章列表
     */
    @RequestMapping("article/list")
    public Result<?> articleList(Integer articleType, Pagination<Article> pagination) {
        return ResultUtils.success(articleService.listArticle(articleType, pagination));
    }

    /**
     * 文章详情
     */
    @RequestMapping("article/{articleId}")
    public Result<?> getArticle(@PathVariable String articleId) {
        return ResultUtils.success(articleService.getArticleById(articleId));
    }

    /**
     * 发布文章
     */
    @RequestMapping("article/create")
    public Result<?> createArticle(HttpServletRequest request, Article article) {
        String userId = JwtUtils.getUserByToken(request.getHeader(JwtUtils.ACCESS_TOKEN_NAME)).getId();
        articleService.createArticle(userId, article);
        return ResultUtils.success();
    }

    /**
     * 收藏/取消收藏文章
     */
    @RequestMapping("article/collect/{articleId}")
    public Result<?> updateCollect(HttpServletRequest request, @PathVariable String articleId) {
        String userId = JwtUtils.getUserByToken(request.getHeader(JwtUtils.ACCESS_TOKEN_NAME)).getId();
        articleService.updateCollection(userId, articleId);
        return ResultUtils.success();
    }

    /**
     * 点赞/取消点赞文章
     */
    @RequestMapping("article/like/{articleId}")
    public Result<?> updateLike(HttpServletRequest request, @PathVariable String articleId) {
        String userId = JwtUtils.getUserByToken(request.getHeader(JwtUtils.ACCESS_TOKEN_NAME)).getId();
        articleService.updateLike(userId, articleId);
        return ResultUtils.success();
    }

    /**
     * 用户收藏文章列表
     */
    @RequestMapping("article/collection/list")
    public Result<?> collectionList(HttpServletRequest request, Pagination pagination) {
        String userId = JwtUtils.getUserByToken(request.getHeader(JwtUtils.ACCESS_TOKEN_NAME)).getId();
        return ResultUtils.success(articleService.listArticleCollection(userId, pagination));
    }


    /**
     * 按美妆品id查询调查模板
     */
    @RequestMapping("investTemplate/get/{orderId}")
    public Result<?> articleList(HttpServletRequest request, @PathVariable String orderId) {
        String userId = JwtUtils.getUserByToken(request.getHeader(JwtUtils.ACCESS_TOKEN_NAME)).getId();
        return ResultUtils.success(investigationService.getInvestigationTemplate(orderId, userId));
    }

    /**
     * 提交调查
     */
    @RequestMapping("invest/create")
    public Result<?> createInvestigation(HttpServletRequest request, Investigation investigation) {
        String userId = JwtUtils.getUserByToken(request.getHeader(JwtUtils.ACCESS_TOKEN_NAME)).getId();
        investigation.setUserId(userId);
        investigationService.createInvestigation(userId, investigation);
        return ResultUtils.success();
    }


}
