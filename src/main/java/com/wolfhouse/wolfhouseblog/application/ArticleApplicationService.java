package com.wolfhouse.wolfhouseblog.application;

import com.mybatisflex.core.query.QueryColumn;
import com.wolfhouse.wolfhouseblog.common.utils.page.PageResult;
import com.wolfhouse.wolfhouseblog.pojo.dto.ArticleQueryPageDto;
import com.wolfhouse.wolfhouseblog.pojo.vo.ArticleBriefVo;
import com.wolfhouse.wolfhouseblog.pojo.vo.ArticleVo;

/**
 * @author linexsong
 */
public interface ArticleApplicationService {
    /**
     * 根据指定的文章 ID 获取对应的文章内容视图对象。
     *
     * @param id 文章的唯一标识符
     * @return 包含文章详细信息的 ArticleVo 对象
     * @throws Exception 如果在获取文章内容时发生异常
     */
    ArticleVo getArticleVoById(Long id) throws Exception;

    /**
     * 根据查询条件分页获取文章视图对象列表。
     *
     * @param dto 文章查询分页参数对象
     * @return 包含文章视图对象的分页结果
     * @throws Exception 如果在查询过程中发生异常
     */
    PageResult<ArticleVo> queryArticleVo(ArticleQueryPageDto dto) throws Exception;

    /**
     * 根据查询条件和指定列分页获取文章视图对象列表。
     *
     * @param dto     文章查询分页参数对象
     * @param columns 需要查询的具体列
     * @return 包含文章视图对象的分页结果
     * @throws Exception 如果在查询过程中发生异常
     */
    PageResult<ArticleVo> queryArticleVo(ArticleQueryPageDto dto, QueryColumn... columns) throws Exception;

    /**
     * 根据查询条件分页获取文章简要信息视图对象列表。
     *
     * @param dto 文章查询分页参数对象
     * @return 包含文章简要信息的分页结果
     * @throws Exception 如果在查询过程中发生异常
     */
    PageResult<ArticleBriefVo> queryArticleBriefVo(ArticleQueryPageDto dto) throws Exception;
}
