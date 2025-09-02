package com.wolfhouse.wolfhouseblog.mapper;

import com.mybatisflex.core.BaseMapper;
import com.wolfhouse.wolfhouseblog.pojo.domain.Article;
import org.apache.ibatis.annotations.Mapper;

import java.util.Set;

/**
 * @author linexsong
 */
@Mapper
public interface ArticleMapper extends BaseMapper<Article> {

    /**
     * 插入文章，并回显 ID
     *
     * @param article 文章对象
     * @return 影响的行数
     */
    Integer insertWithPkBack(Article article);


    /**
     * 移除用户的标签关联
     *
     * @param userId 用户ID
     * @param tagIds 要移除的标签ID集合
     * @return 影响的行数
     */
    Integer removeTags(Long userId, Set<Long> tagIds);
}
