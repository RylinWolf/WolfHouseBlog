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
        ArticleTableDef.ARTICLE.VISIBILITY,
        ArticleTableDef.ARTICLE.PRIMARY,
        ArticleTableDef.ARTICLE.AUTHOR_ID,
        ArticleTableDef.ARTICLE.POST_TIME};

    public static final String POST_FAILED = "文章发布失败！";
    public static final String UPDATE_FAILED = "文章修改失败！";
    public static final String ACCESS_DENIED = "无法访问该文章！";
    public static final String DELETE_FAILED = "文章删除失败！";
    public static final String UPDATE_PARTITION_FAILED = "文章分区更新失败！";
    public static final String COMMENT_NOT_EXIST = "评论不存在！";
    public static final String COMMENT_FAILED = "评论发布失败！";
    public static final String COMMENT_DELETE_FAILED = "评论删除失败！";
    public static final String NOT_DRAFTED = "文章未暂存！";
    public static final String DRAFT_FAILED = "暂存失败！";
    public static final String DRAFT_EXIST = "已有其他文章暂存！";

    public static final String ALREADY_LIKED = "文章已点赞！";
    public static final String NOT_LIKED = "文章未点赞！";
}
