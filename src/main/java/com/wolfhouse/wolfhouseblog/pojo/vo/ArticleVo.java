package com.wolfhouse.wolfhouseblog.pojo.vo;

import com.wolfhouse.wolfhouseblog.common.enums.VisibilityEnum;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

/**
 * @author linexsong
 */
@Data
public class ArticleVo {
    private Long id;
    private String title;
    private String primary;
    private Long authorId;
    private String content;
    private LocalDateTime postTime;
    private Long partitionId;
    private List<String> tags;
    private List<Long> comUseTags;
    private VisibilityEnum visibility;
}
