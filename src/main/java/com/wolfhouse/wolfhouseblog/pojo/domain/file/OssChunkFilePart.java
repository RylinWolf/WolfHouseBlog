package com.wolfhouse.wolfhouseblog.pojo.domain.file;

import com.mybatisflex.annotation.Id;
import com.mybatisflex.annotation.Table;
import lombok.Data;

/**
 * @author linexsong
 */
@Table("oss_chunk_file_part")
@Data
public class OssChunkFilePart {
    @Id
    private String uploadId;
    @Id
    private Long chunkNumber;
    private String eTag;
    private Boolean isCompleted;
}
