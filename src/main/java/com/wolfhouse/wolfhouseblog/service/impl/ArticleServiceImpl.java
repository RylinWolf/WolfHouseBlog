package com.wolfhouse.wolfhouseblog.service.impl;

import com.mybatisflex.core.paginate.Page;
import com.mybatisflex.core.query.QueryWrapper;
import com.mybatisflex.spring.service.impl.ServiceImpl;
import com.wolfhouse.wolfhouseblog.common.enums.VisibilityEnum;
import com.wolfhouse.wolfhouseblog.common.utils.ServiceUtil;
import com.wolfhouse.wolfhouseblog.common.utils.page.PageResult;
import com.wolfhouse.wolfhouseblog.mapper.ArticleMapper;
import com.wolfhouse.wolfhouseblog.pojo.domain.Article;
import com.wolfhouse.wolfhouseblog.pojo.dto.ArticleQueryPageDto;
import com.wolfhouse.wolfhouseblog.pojo.vo.ArticleVo;
import com.wolfhouse.wolfhouseblog.service.ArticleService;
import org.springframework.stereotype.Service;

/**
 * @author linexsong
 */
@Service
public class ArticleServiceImpl extends ServiceImpl<ArticleMapper, Article> implements ArticleService {
    @Override
    public PageResult<ArticleVo> getQuery(ArticleQueryPageDto dto) {
        Page<Article> page = mapper.paginate(
                dto.getPageNumber(), dto.getPageSize(),
                new QueryWrapper()
                        // 查询当前用户的私人日记和全部公开日记
                        .and(q -> {
                            q.eq(Article::getVisibility, VisibilityEnum.PUBLIC);
                            var userId = ServiceUtil.loginUser();
                            if (userId != null) {
                                q.or(q2 -> {
                                    q.eq(Article::getVisibility, VisibilityEnum.PRIVATE)
                                     .eq(Article::getAuthorId, userId);
                                });
                            }
                        })
                        .eq(Article::getId, dto.getId())
                        // 按标题查询
                        .like(Article::getTitle, dto.getTitle())
                        // 日期范围查询
                        .between(Article::getPostTime, dto.getPostStart(), dto.getPostEnd())
                                            );
        
        return PageResult.of(page, ArticleVo.class);
    }

}
