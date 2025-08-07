package com.wolfhouse.wolfhouseblog.pojo.dto;

import lombok.Data;
import org.openapitools.jackson.nullable.JsonNullable;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @author linexsong
 */
@Component
@Data
public class AdminUpdateDto {
    private Long id;
    private JsonNullable<String> name = JsonNullable.undefined();
    private JsonNullable<List<Long>> authorities = JsonNullable.undefined();
}
