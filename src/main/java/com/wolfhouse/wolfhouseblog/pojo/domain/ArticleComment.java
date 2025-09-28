package com.wolfhouse.wolfhouseblog.pojo.domain;

import com.mybatisflex.annotation.Id;
import com.mybatisflex.annotation.KeyType;
import com.mybatisflex.annotation.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * @author linexsong
 */
@Table("article_comment")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ArticleComment {
    @Id(keyType = KeyType.Auto)
    private Long id;
    private Long userId;
    private Long articleId;
    private Long replyId;
    private String content;
    private LocalDateTime commentTime;
}
