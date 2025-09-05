package com.wolfhouse.wolfhouseblog.pojo.dto.file;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * 分片上传请求 Dto
 *
 * @author linexsong
 */
@Data
public class ChunkFilePermitDto {
    /** 分片号 */
    @NotNull
    private Long chunkNumber;

    /** 分片数量 */
    @NotNull
    private Long chunkCount;

    /** 分片大小 */
    @NotNull
    private Long chunkSize;

    /** 文件名 */
    @NotNull
    @Size(min = 1, max = 20)
    private String filename;

    /** 文件类型 */
    @NotNull
    private String type;

    /** 文件大小 */
    @NotNull
    private Long size;
}
