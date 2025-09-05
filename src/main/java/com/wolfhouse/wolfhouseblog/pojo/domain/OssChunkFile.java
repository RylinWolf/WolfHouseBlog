package com.wolfhouse.wolfhouseblog.pojo.domain;

import com.mybatisflex.annotation.Id;
import com.mybatisflex.annotation.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author linexsong
 */
@Table("oss_chunk_file")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class OssChunkFile {
    @Id
    private String uploadId;
    @Id
    private Long chunkNumber;
    private Long chunkCount;
    private Long chunkSize;
    private String filename;
    private String type;
    private Long size;
}
