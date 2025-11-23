package com.wolfhouse.wolfhouseblog.pojo.domain;

import com.mybatisflex.annotation.Column;
import com.mybatisflex.annotation.Id;
import com.mybatisflex.annotation.KeyType;
import com.mybatisflex.annotation.Table;
import com.wolfhouse.wolfhouseblog.common.enums.oss.FileType;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

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
     * 文件在 OSS 中存储的路径
     */
    @Schema(description = "文件 OSS 存储路径")
    @Column(value = "file_oss_path")
    private String fileOssPath;

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
    private LocalDateTime uploadTime;

    /**
     * 文件业务类型
     */
    @Schema(description = "文件业务类型")
    @Column(value = "file_type")
    private FileType fileType;

    /**
     * 文件删除
     */
    @Schema(description = "文件是否已删除")
    @Column(value = "file_deleted", isLogicDelete = true)
    private Integer fileDeleted;

}
