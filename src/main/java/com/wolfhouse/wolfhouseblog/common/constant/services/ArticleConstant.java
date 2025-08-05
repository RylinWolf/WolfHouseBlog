package com.wolfhouse.wolfhouseblog.common.constant.services;

import com.mybatisflex.core.query.QueryColumn;
import com.wolfhouse.wolfhouseblog.pojo.domain.table.ArticleTableDef;

/**
 * @author linexsong
 */
public class ArticleConstant {
    public static final QueryColumn[] BRIEF_COLUMNS = {
            ArticleTableDef.ARTICLE.ID,
            ArticleTableDef.ARTICLE.TITLE,
            ArticleTableDef.ARTICLE.PRIMARY,
            ArticleTableDef.ARTICLE.AUTHOR_ID,
            ArticleTableDef.ARTICLE.POST_TIME};

    public static final String POST_FAILED = "文章发布失败！";
    public static final String UPDATE_FAILED = "文章修改失败！";
}
