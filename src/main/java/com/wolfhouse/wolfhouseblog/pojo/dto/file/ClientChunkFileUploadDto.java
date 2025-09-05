package com.wolfhouse.wolfhouseblog.pojo.dto.file;

import lombok.Data;

import java.io.InputStream;

/**
 * @author linexsong
 */
@Data
public class ClientChunkFileUploadDto {
    /** 上传的对象名称 */
    private String objectName;
    /** 文件流 */
    private InputStream ins;
    /** 分区号 */
    private Long partNumber;
    /** 上传 ID */
    private String uploadId;
    /** 内容 MD5 校验 */
    private String contentMd5;
}
