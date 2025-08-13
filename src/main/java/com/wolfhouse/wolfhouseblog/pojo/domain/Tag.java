package com.wolfhouse.wolfhouseblog.pojo.domain;

import com.mybatisflex.annotation.Id;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 常用标签实体类
 *
 * @author linexsong
 */
@Schema(name = "常用标签")
@Data
public class Tag {
    @Id
    private Long id;
    private String name;
}
