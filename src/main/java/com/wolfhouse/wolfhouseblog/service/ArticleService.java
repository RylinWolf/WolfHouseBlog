package com.wolfhouse.wolfhouseblog.service;

import com.mybatisflex.core.service.IService;
import com.wolfhouse.wolfhouseblog.common.utils.page.PageResult;
import com.wolfhouse.wolfhouseblog.pojo.domain.Article;
import com.wolfhouse.wolfhouseblog.pojo.dto.ArticleQueryPageDto;
import com.wolfhouse.wolfhouseblog.pojo.vo.ArticleVo;

/**
 * @author linexsong
 */
public interface ArticleService extends IService<Article> {
    PageResult<ArticleVo> getQuery(ArticleQueryPageDto dto);
}
