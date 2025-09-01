package com.wolfhouse.wolfhouseblog.pojo.domain;

import com.mybatisflex.annotation.Id;
import com.mybatisflex.annotation.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author linexsong
 */
@Table("article_draft")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ArticleDraft {
    @Id
    private Long id;
    private Long authorId;
    private Long articleId;
}
