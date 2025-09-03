package com.wolfhouse.wolfhouseblog.pojo.dto;

import com.wolfhouse.wolfhouseblog.common.enums.VisibilityEnum;
import lombok.Data;

import java.util.List;

/**
 * @author linexsong
 */
@Data
public class ArticleDraftDto {
    private Long id;
    private String title;
    private String primary;
    private String content;
    private VisibilityEnum visibility;
    private Long partitionId;
    private List<String> tags;
    private List<Long> comUseTags;
}
