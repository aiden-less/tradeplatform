package com.converage.controller.admin;

import com.converage.architecture.dto.Pagination;
import com.converage.architecture.dto.Result;
import com.converage.architecture.utils.JwtUtils;
import com.converage.architecture.utils.ResultUtils;
import com.converage.entity.information.Article;
import com.converage.entity.information.Investigation;
import com.converage.service.information.ArticleService;
import com.converage.service.information.InvestigationService;
import com.google.common.collect.ImmutableMap;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

/**
 * 资讯文章 和 调查问卷
 */
@RestController
@RequestMapping("admin/information")
public class AdminInformationController {

    @Autowired
    private ArticleService articleService;

    @Autowired
    private InvestigationService investigationService;

    /**
     * 文章 列表
     */
    @PostMapping("article/list")
    public Result<?> articleList(@RequestBody Pagination<Article> pagination) {
        return ResultUtils.success(articleService.getByPage(pagination), pagination.getTotalRecordNumber());
    }

    /**
     * 单个 获取单个
     */
    @GetMapping("article/get/{id}")
    public Result<?> get(@PathVariable String id) {
        return ResultUtils.success(articleService.selectOneById(id, Article.class));
    }

    /**
     * 文章 删除
     */
    @GetMapping("article/delete/{id}")
    public Result<?> delete(@PathVariable String id) {
        Article article = new Article();
        article.setId(id);
        int result = articleService.delete(article);
        if (result > 0) {
            return ResultUtils.success();
        } else {
            return ResultUtils.error("操作失败");
        }
    }

    /**
     * 文章 更新添加
     */
    @PostMapping("article/save")
    public Result<?> save(Article article, HttpServletRequest request) {
        String userId = JwtUtils.getAdminByToken(request.getHeader(JwtUtils.ACCESS_TOKEN_NAME)).getId();
        String id = article.getId();
        int result = 0;
        if ("0".equals(id)) {
            article.setCreateBy(userId);
            result = articleService.insertIfNotNull(article);
        } else {
            result = articleService.updateIfNotNull(article);
        }
        if (result > 0) {
            return ResultUtils.success();
        } else {
            return ResultUtils.error("操作失败");
        }
    }

    /**
     * 问卷调查 添加更新
     */
    @PostMapping("investigationSave/{id}")
    public Result<?> saveInvestigation(@PathVariable String id, Investigation investigation) {
        int result = 0;
        String templateId = "0";
        if (templateId.equals(id)) {
            //添加
            String beautyId = investigation.getBeautyId();
            Map<String, Object> whereMap = ImmutableMap.of(Investigation.Beauty_id + " = ", beautyId, Investigation.Template_id + " = ", templateId);
            if (investigationService.selectOneByWhereMap(whereMap, Investigation.class) != null) {
                return ResultUtils.error("该商品已经存在问卷");
            }
            result = investigationService.insertIfNotNull(investigation);
        } else {
            result = investigationService.updateIfNotNull(investigation);
        }
        if (result > 0) {
            return ResultUtils.success();
        } else {
            return ResultUtils.error("操作失败");
        }
    }

    /**
     * 问卷调查 列表
     */
    @PostMapping("investigationList")
    public Result<?> list(@RequestBody Pagination<Investigation> pagination) {
        return ResultUtils.success(investigationService.getByPage(pagination), pagination.getTotalRecordNumber());
    }
}
