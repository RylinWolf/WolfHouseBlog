package com.wolfhouse.wolfhouseblog.pojo.domain;

import com.mybatisflex.annotation.Column;
import com.mybatisflex.annotation.Id;
import com.mybatisflex.annotation.KeyType;
import com.mybatisflex.annotation.Table;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

/**
 * 实体类。
 *
 * @author Rylin Wolf
 * @since 1.0
 */
@Data
@Schema(name = "$table.comment")
@Table(value = "oss_upload_log")
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class OssUploadLog {

    /**
     * 日志 ID
     */
    @Schema(description = "日志 ID")
    @Id(keyType = KeyType.Auto)
    private Long id;

    /**
     * 文件名
     */
    @Schema(description = "文件名")
    @Column(value = "filename")
    private String filename;

    /**
     * 文件大小
     */
    @Schema(description = "文件大小")
    @Column(value = "file_size")
    private Long fileSize;

    /**
     * 文件路径
     */
    @Schema(description = "文件路径")
    @Column(value = "file_path")
    private String filePath;

    /**
     * 上传用户
     */
    @Schema(description = "上传用户")
    @Column(value = "upload_user")
    private Long uploadUser;

    /**
     * 上传时间
     */
    @Schema(description = "上传时间")
    @Column(value = "upload_time")
    private Date uploadTime;


}
