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
    // TODO 在用到的地方注入作者名和分区名

    private Long id;
    private String title;
    private String primary;
    private UserBriefVo author;
    private String content;
    private LocalDateTime postTime;
    private Long partitionId;
    private String partitionName;
    private List<String> tags;
    private List<Long> comUseTags;
    private VisibilityEnum visibility;
    /** 浏览量 */
    private Long views;
}
