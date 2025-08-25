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
import jakarta.validation.Valid;

/**
 * @author linexsong
 */
public interface ArticleService extends IService<Article> {

    /**
     * 根据条件分页查询文章
     *
     * @param dto     查询条件
     * @param columns 查询的列
     * @return 分页查询结果
     */
    Page<Article> queryBy(ArticleQueryPageDto dto, QueryColumn... columns) throws Exception;

    /**
     * 获取文章简要信息的分页查询
     *
     * @param dto 查询条件
     * @return 文章简要信息的分页结果
     */
    PageResult<ArticleBriefVo> getBriefQuery(ArticleQueryPageDto dto) throws Exception;

    /**
     * 获取文章详细信息的分页查询
     *
     * @param dto     查询条件
     * @param columns 查询的列
     * @return 文章详细信息的分页结果
     */
    PageResult<ArticleVo> getQuery(ArticleQueryPageDto dto, QueryColumn... columns) throws Exception;

    /**
     * 通过ID获取文章详情
     *
     * @param id 文章ID
     * @return 文章详细信息
     * @throws Exception 查询失败时抛出异常
     */
    ArticleVo getVoById(Long id) throws Exception;

    /**
     * 发布新文章
     *
     * @param dto 文章信息
     * @return 已发布的文章信息
     * @throws Exception 发布失败时抛出异常
     */
    ArticleVo post(@Valid ArticleDto dto) throws Exception;

    /**
     * 更新文章信息
     *
     * @param dto 更新的文章信息
     * @return 更新后的文章信息
     * @throws Exception 更新失败时抛出异常
     */
    ArticleVo update(ArticleUpdateDto dto) throws Exception;

    /**
     * 通过ID删除文章
     *
     * @param id 文章ID
     * @return 删除是否成功
     * @throws Exception 删除失败时抛出异常
     */
    Boolean deleteById(Long id) throws Exception;

    Boolean isArticleReachable(Long userId, Long articleId) throws Exception;
}
