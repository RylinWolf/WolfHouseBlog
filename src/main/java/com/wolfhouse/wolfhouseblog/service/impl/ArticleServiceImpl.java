package com.wolfhouse.wolfhouseblog.service.impl;

import com.mybatisflex.core.paginate.Page;
import com.mybatisflex.core.query.QueryColumn;
import com.mybatisflex.core.query.QueryWrapper;
import com.mybatisflex.spring.service.impl.ServiceImpl;
import com.wolfhouse.wolfhouseblog.common.constant.services.ArticleConstant;
import com.wolfhouse.wolfhouseblog.common.enums.VisibilityEnum;
import com.wolfhouse.wolfhouseblog.common.utils.ServiceUtil;
import com.wolfhouse.wolfhouseblog.common.utils.page.PageResult;
import com.wolfhouse.wolfhouseblog.mapper.ArticleMapper;
import com.wolfhouse.wolfhouseblog.pojo.domain.Article;
import com.wolfhouse.wolfhouseblog.pojo.dto.ArticleDto;
import com.wolfhouse.wolfhouseblog.pojo.dto.ArticleQueryPageDto;
import com.wolfhouse.wolfhouseblog.pojo.vo.ArticleBriefVo;
import com.wolfhouse.wolfhouseblog.pojo.vo.ArticleVo;
import com.wolfhouse.wolfhouseblog.service.ArticleService;
import org.springframework.stereotype.Service;

/**
 * @author linexsong
 */
@Service
public class ArticleServiceImpl extends ServiceImpl<ArticleMapper, Article> implements ArticleService {

    @Override
    public Page<Article> queryBy(ArticleQueryPageDto dto, QueryColumn... columns) {
        return mapper.paginate(
                dto.getPageNumber(), dto.getPageSize(),
                QueryWrapper.create()
                            .select(columns)
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
                            .between(Article::getPostTime, dto.getPostStart(), dto.getPostEnd()));
    }

    @Override
    public PageResult<ArticleVo> getQuery(ArticleQueryPageDto dto, QueryColumn... columns) {
        return PageResult.of(queryBy(dto, columns), ArticleVo.class);
    }

    @Override
    public PageResult<ArticleBriefVo> getBriefQuery(ArticleQueryPageDto dto) {
        return PageResult.of(queryBy(dto, ArticleConstant.BRIEF_COLUMNS), ArticleBriefVo.class);
    }

    @Override
    public ArticleVo post(ArticleDto dto) {
        return null;
    }
}
