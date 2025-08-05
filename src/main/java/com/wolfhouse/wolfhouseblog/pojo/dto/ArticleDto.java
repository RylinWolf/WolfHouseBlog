package com.wolfhouse.wolfhouseblog.pojo.dto;

import com.wolfhouse.wolfhouseblog.common.enums.VisibilityEnum;
import lombok.Data;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @author linexsong
 */
@Data
@Component
public class ArticleDto {
    private String title;
    private String primary;
    private String content;
    private VisibilityEnum visibility;
    private List<String> tags;
    private List<Long> comUseTags;
}
