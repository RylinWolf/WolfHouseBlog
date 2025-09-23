package com.wolfhouse.wolfhouseblog.pojo.dto;

import com.wolfhouse.wolfhouseblog.common.utils.page.PageDto;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.openapitools.jackson.nullable.JsonNullable;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

/**
 * @author linexsong
 */
@Component
@Data
@EqualsAndHashCode(callSuper = true)
public class ArticleQueryPageDto extends PageDto {
    private JsonNullable<Long> id = JsonNullable.undefined();
    private JsonNullable<String> title = JsonNullable.undefined();
    private JsonNullable<Long> authorId = JsonNullable.undefined();
    private JsonNullable<LocalDateTime> postStart = JsonNullable.undefined();
    private JsonNullable<LocalDateTime> postEnd = JsonNullable.undefined();
    private JsonNullable<Long> partitionId = JsonNullable.undefined();
    private JsonNullable<String> content = JsonNullable.undefined();
    /** 激活高亮字段时，查询结果对象中，支持高亮查询的字段会自动有高亮显示 */
    private Boolean highlight;

    @Override
    public String toString() {
        return "ArticleQueryPageDto{" +
               "id=" + id +
               ", title=" + title +
               ", authorId=" + authorId +
               ", postStart=" + postStart +
               ", postEnd=" + postEnd +
               ", partitionId=" + partitionId +
               ", pageNumber=" + pageNumber +
               ", pageSize=" + pageSize +
               ", content=" + content +
               '}';
    }
}
