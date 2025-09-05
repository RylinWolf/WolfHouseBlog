package com.wolfhouse.wolfhouseblog.pojo.dto.file;

import lombok.Data;

/**
 * 分片上传请求 Dto
 *
 * @author linexsong
 */
@Data
public class ChunkFilePermitDto {
    /** 分片号 */
    private Long chunkNumber;
    /** 分片数量 */
    private Long chunkCount;
    /** 分片大小 */
    private Long chunkSize;
    /** 文件名 */
    private String filename;
    /** 文件类型 */
    private String type;
    /** 文件大小 */
    private Long size;
}
