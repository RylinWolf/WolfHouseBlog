package com.wolfhouse.wolfhouseblog.pojo.dto.es;

import com.wolfhouse.wolfhouseblog.common.enums.VisibilityEnum;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

/**
 * @author linexsong
 */
@Data
public class ArticleEsDto {
    private Long id;
    private String title;
    private String primary;
    private Long authorId;
    private String content;
    private LocalDateTime postTime;
    private LocalDateTime editTime;
    private Long partitionId;
    private String partitionName;
    private List<String> tags;
    private List<Long> comUseTags;
    private VisibilityEnum visibility;
    /** 浏览量 */
    private Long views;
    /** 点赞量 */
    private Long likeCount;
}
