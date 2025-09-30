package com.wolfhouse.wolfhouseblog.pojo.vo;

import com.wolfhouse.wolfhouseblog.common.enums.VisibilityEnum;
import lombok.Data;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

/**
 * @author linexsong
 */
@Data
@Component
public class ArticleBriefVo {
    private Long id;
    private String title;
    private String primary;
    private Long authorId;
    private String authorName;
    private LocalDateTime postTime;
    private VisibilityEnum visibility;
    /** 浏览量 */
    private Long views;
}
