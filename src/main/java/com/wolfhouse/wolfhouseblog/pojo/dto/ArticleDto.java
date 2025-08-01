package com.wolfhouse.wolfhouseblog.pojo.dto;

import com.wolfhouse.wolfhouseblog.common.enums.VisibilityEnum;
import com.wolfhouse.wolfhouseblog.pojo.domain.Tag;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @author linexsong
 */
@Component
public class ArticleDto {
    private String title;
    private String primary;
    private String content;
    private VisibilityEnum visibility;
    private List<String> tags;
    private List<Tag> comUseTags;
}
