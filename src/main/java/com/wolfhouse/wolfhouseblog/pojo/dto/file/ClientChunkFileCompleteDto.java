package com.wolfhouse.wolfhouseblog.pojo.dto.file;

import com.aliyun.sdk.service.oss2.models.Part;
import lombok.Data;

import java.util.List;

/**
 * @author linexsong
 */
@Data
public class ClientChunkFileCompleteDto {
    private String objectName;
    private String uploadId;
    private List<Part> eTags;
}
