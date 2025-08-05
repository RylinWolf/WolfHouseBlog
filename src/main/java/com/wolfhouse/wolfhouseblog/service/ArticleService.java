package com.wolfhouse.wolfhouseblog.service;

import com.mybatisflex.core.paginate.Page;
import com.mybatisflex.core.query.QueryColumn;
import com.mybatisflex.core.service.IService;
import com.wolfhouse.wolfhouseblog.common.utils.page.PageResult;
import com.wolfhouse.wolfhouseblog.pojo.domain.Article;
import com.wolfhouse.wolfhouseblog.pojo.dto.ArticleDto;
import com.wolfhouse.wolfhouseblog.pojo.dto.ArticleQueryPageDto;
import com.wolfhouse.wolfhouseblog.pojo.dto.ArticleUpdateDto;
import com.wolfhouse.wolfhouseblog.pojo.vo.ArticleBriefVo;
import com.wolfhouse.wolfhouseblog.pojo.vo.ArticleVo;

/**
 * @author linexsong
 */
public interface ArticleService extends IService<Article> {
    Page<Article> queryBy(ArticleQueryPageDto dto, QueryColumn... columns);

    PageResult<ArticleBriefVo> getBriefQuery(ArticleQueryPageDto dto);

    PageResult<ArticleVo> getQuery(ArticleQueryPageDto dto, QueryColumn... columns);

    ArticleVo getById(Long id) throws Exception;

    ArticleVo post(ArticleDto dto) throws Exception;

    ArticleVo update(ArticleUpdateDto dto) throws Exception;

    Boolean deleteById(Long id) throws Exception;
}
