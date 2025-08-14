package com.wolfhouse.wolfhouseblog.pojo.dto;

import com.wolfhouse.wolfhouseblog.common.enums.VisibilityEnum;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @author linexsong
 */
@Data
@Component
public class ArticleDto {
    @NotNull
    @Size(min = 1, max = 20)
    private String title;
    
    @Size(max = 200)
    private String primary;

    @NotNull
    @Size(min = 1, max = 2000)
    private String content;
    private VisibilityEnum visibility;
    private List<String> tags;
    private List<Long> comUseTags;
}
