package com.wolfhouse.wolfhouseblog.pojo.domain;

import com.wolfhouse.wolfhouseblog.common.enums.VisibilityEnum;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 文章实体类
 *
 * @author linexsong
 */
@Schema(name = "文章")
@Data
public class Article {
    private Long id;
    private String title;
    private String primary;
    private Long authorId;
    private String content;
    private LocalDateTime postTime;
    private LocalDateTime editTime;
    private VisibilityEnum visibility;
    private Long partitionId;
    private List<String> tags;
    private List<Long> comUseTags;
}
