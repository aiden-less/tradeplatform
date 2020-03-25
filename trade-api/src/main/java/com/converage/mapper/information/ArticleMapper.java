package com.converage.mapper.information;

import com.converage.architecture.dto.Pagination;
import com.converage.entity.information.Article;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ArticleMapper {

    List<Article> selectByPage(Pagination<Article> pagination);

}
