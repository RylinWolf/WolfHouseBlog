package com.wolfhouse.wolfhouseblog.pojo.vo.file;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author linexsong
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ChunkFilePermitVo {
    private String uploadId;
    private Integer statusCode;
}
