package com.wolfhouse.wolfhouseblog.service;

import com.mybatisflex.core.paginate.Page;
import com.mybatisflex.core.query.QueryColumn;
import com.mybatisflex.core.service.IService;
import com.wolfhouse.wolfhouseblog.common.utils.page.PageResult;
import com.wolfhouse.wolfhouseblog.pojo.domain.Article;
import com.wolfhouse.wolfhouseblog.pojo.dto.ArticleDraftDto;
import com.wolfhouse.wolfhouseblog.pojo.dto.ArticleDto;
import com.wolfhouse.wolfhouseblog.pojo.dto.ArticleQueryPageDto;
import com.wolfhouse.wolfhouseblog.pojo.dto.ArticleUpdateDto;
import com.wolfhouse.wolfhouseblog.pojo.vo.ArticleBriefVo;
import com.wolfhouse.wolfhouseblog.pojo.vo.ArticleVo;
import jakarta.validation.Valid;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * 文章服务接口，提供文章的增删改查等基本操作
 *
 * @author linexsong
 */
public interface ArticleService extends IService<Article> {

    /**
     * 根据条件分页查询文章
     * 支持多条件组合查询，返回完整的文章信息
     *
     * @param dto     查询条件对象
     * @param columns 需要查询的数据列
     * @return 文章分页查询结果
     * @throws Exception 查询过程中可能出现的异常
     */
    Page<Article> queryBy(ArticleQueryPageDto dto, QueryColumn... columns) throws Exception;

    /**
     * 获取文章简要信息的分页查询
     * 仅返回文章的基本信息，用于列表展示
     *
     * @param dto 分页查询条件对象
     * @return 文章简要信息的分页结果集
     * @throws Exception 查询过程中可能出现的异常
     */
    PageResult<ArticleBriefVo> getBriefQuery(ArticleQueryPageDto dto) throws Exception;

    /**
     * 根据指定的文章ID集合，获取对应的文章简要信息结果。
     *
     * @param articleIds 文章ID集合，用于查询文章简要信息
     * @return 包含文章简要信息的结果
     * @throws Exception 查询过程中可能出现的异常
     */
    List<ArticleBriefVo> getBriefByIds(Collection<Long> articleIds) throws Exception;

    /**
     * 获取文章详细信息的分页查询
     * 返回文章的完整信息，包括内容和相关元数据
     *
     * @param dto     分页查询条件对象
     * @param columns 需要查询的数据列
     * @return 文章详细信息的分页结果集
     * @throws Exception 查询过程中可能出现的异常
     */
    PageResult<ArticleVo> getQueryVo(ArticleQueryPageDto dto, QueryColumn... columns) throws Exception;

    /**
     * 通过ID获取文章详情
     * 获取单篇文章的所有信息
     *
     * @param id 文章的唯一标识ID
     * @return 文章的详细信息视图对象
     * @throws Exception 当文章不存在或查询失败时抛出异常
     */
    ArticleVo getVoById(Long id) throws Exception;

    /**
     * 发布新文章
     * 创建并保存新的文章记录
     *
     * @param dto 文章信息数据传输对象
     * @return 发布成功后的文章详情视图对象
     * @throws Exception 当文章数据验证失败或保存失败时抛出异常
     */
    Article post(@Valid ArticleDto dto) throws Exception;

    /**
     * 保存文章草稿
     * 将当前编辑的文章内容保存为草稿状态
     *
     * @param dto 文章草稿数据传输对象
     * @return 保存后的文章草稿视图对象
     * @throws Exception 当保存草稿操作失败时抛出异常
     */
    ArticleVo draft(ArticleDraftDto dto) throws Exception;

    /**
     * 获取当前用户的文章草稿
     * 读取用户最近保存的文章草稿内容
     *
     * @return 文章草稿视图对象
     * @throws Exception 当获取草稿失败时抛出异常
     */
    ArticleVo getDraft() throws Exception;

    /**
     * 删除当前用户的文章草稿
     * 清除用户最近保存的文章草稿
     *
     * @return 删除操作的执行结果，true表示成功，false表示失败
     * @throws Exception 当删除草稿操作失败时抛出异常
     */
    Boolean unDraft() throws Exception;


    /**
     * 更新文章信息
     * 修改现有文章的内容或元数据
     *
     * @param dto 更新的文章信息数据传输对象
     * @return 更新后的文章详情视图对象
     * @throws Exception 当文章不存在或更新操作失败时抛出异常
     */
    Article update(ArticleUpdateDto dto) throws Exception;

    /**
     * 通过ID删除文章
     * 根据文章ID删除对应的文章记录
     *
     * @param id 待删除文章的唯一标识ID
     * @return 删除操作的执行结果，true表示成功，false表示失败
     * @throws Exception 当文章不存在或删除操作失败时抛出异常
     */
    Boolean deleteById(Long id) throws Exception;

    /**
     * 检查用户是否可以访问指定文章
     * 验证用户对特定文章的访问权限
     *
     * @param userId    用户ID
     * @param articleId 文章ID
     * @return 是否可访问，true表示可以访问，false表示不能访问
     * @throws Exception 当检查过程发生错误时抛出异常
     */
    Boolean isArticleReachable(Long userId, Long articleId) throws Exception;

    /**
     * 检查用户是否是文章的所有者
     * 验证指定用户是否拥有文章的所有权
     *
     * @param articleId 文章ID
     * @param login     用户登录ID
     * @return 是否为文章所有者，true表示是，false表示否
     */
    Boolean isArticleOwner(Long articleId, Long login);

    /**
     * 设置文章的浏览次数。
     *
     * @param views 包含文章ID和对应浏览次数的映射表，其中键为文章ID，值为浏览次数
     * @return 成功设置浏览次数的文章数量
     */
    Set<Long> addViews(Map<String, Long> views);

    /**
     * 增加指定文章的浏览次数。
     *
     * @param articleId 文章的唯一标识ID
     * @param views     新增的浏览次数
     * @return 更新是否成功
     */
    Boolean addViews(Long articleId, Long views);
}
