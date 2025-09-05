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
public class FileUploadResultVo {
    /** 上传完成的分片标记 */
    private String eTag;
    /** 上传完成的分片号 */
    private Long partNumber;
    /** 上传完成的状态码 */
    private Integer statusCode;
}
