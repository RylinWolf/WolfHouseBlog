package com.wolfhouse.wolfhouseblog.application;

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


}
