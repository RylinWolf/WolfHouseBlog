package com.wolfhouse.wolfhouseblog.service;

import com.wolfhouse.wolfhouseblog.pojo.dto.file.ChunkFilePermitDto;
import com.wolfhouse.wolfhouseblog.pojo.vo.file.ChunkFilePermitVo;
import com.wolfhouse.wolfhouseblog.pojo.vo.file.FileUploadResultVo;

import java.io.InputStream;

/**
 * @author linexsong
 */
public interface FileService {
    /**
     * 上传用户头像文件
     *
     * @param uploadId 上传 Id
     * @param ins      要上传的头像文件输入流
     * @return 上传成功后的头像文件访问URL
     */
    FileUploadResultVo uploadAvatar(Long uploadId, InputStream ins);

    /**
     * 开始用户头像文件分块上传。
     *
     * @param dto 分块上传的准许信息数据传输对象，包含分块编号等上传必要信息。
     * @return 分块文件上传准许信息视图对象，包含上传所需的相关信息。
     */
    ChunkFilePermitVo startUploadAvatar(ChunkFilePermitDto dto);

    /**
     * 完成用户头像文件的上传操作。
     *
     * @param uploadId 上传任务的唯一标识符
     * @return 包含上传结果的文件上传结果视图对象
     */
    FileUploadResultVo completeUploadAvatar(Long uploadId);

    /**
     * 停止用户上传头像文件的操作。
     *
     * @param uploadId 上传任务的唯一标识符
     * @return 包含上传结果的文件上传结果视图对象
     */
    FileUploadResultVo abortUploadAvatar(Long uploadId);
}
