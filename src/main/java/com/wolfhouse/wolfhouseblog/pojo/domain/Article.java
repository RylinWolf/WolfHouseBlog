package com.wolfhouse.wolfhouseblog.pojo.domain;

import com.mybatisflex.annotation.Column;
import com.mybatisflex.annotation.Id;
import com.mybatisflex.annotation.KeyType;
import com.mybatisflex.annotation.Table;
import com.mybatisflex.core.handler.JacksonTypeHandler;
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
@Table("article")
public class Article {
    @Id(keyType = KeyType.Auto)
    private Long id;
    private String title;
    private String primary;
    private Long authorId;
    private String content;
    private LocalDateTime postTime;
    private LocalDateTime editTime;
    private VisibilityEnum visibility;
    private Long partitionId;
    @Column(typeHandler = JacksonTypeHandler.class)
    private List<String> tags;
    @Column(typeHandler = JacksonTypeHandler.class)
    private List<Long> comUseTags;
}
