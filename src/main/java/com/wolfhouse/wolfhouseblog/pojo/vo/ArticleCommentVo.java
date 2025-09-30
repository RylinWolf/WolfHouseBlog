package com.wolfhouse.wolfhouseblog.pojo.vo;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * @author linexsong
 */
@Data
public class ArticleCommentVo {
    private Long id;
    private Long userId;
    private Long replyId;
    private String content;
    private LocalDateTime commentTime;
}
