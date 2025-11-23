package com.wolfhouse.wolfhouseblog.service;


import com.mybatisflex.core.service.IService;
import com.wolfhouse.wolfhouseblog.common.enums.oss.FileType;
import com.wolfhouse.wolfhouseblog.pojo.domain.OssUploadLog;

import java.util.Set;

/**
 * 服务层。
 *
 * @author Rylin Wolf
 * @since 1.0
 */
public interface OssUploadLogService extends IService<OssUploadLog> {
    /**
     * 记录文件上传日志信息的方法。
     *
     * @param filename 上传的文件名
     * @param size     文件的大小（以字节为单位）
     * @param filepath 文件的访问路径
     * @param ossPath  文件在 OSS 中存储的路径
     * @param fileType 文件的业务类型
     * @return 是否记录成功
     */
    boolean log(String filename, Long size, String filepath, String ossPath, FileType fileType);

    /**
     * 获取指定用户未被删除的文件 Oss 路径列表。
     *
     * @param userId 用户的唯一标识
     * @return 未被删除的文件 Oss 路径列表
     */
    Set<String> getUndeletedAvatarOssPath(Long userId);

    /**
     * 获取重复上传的记录（排除最近一条）
     *
     * @return 重复上传的记录，若无记录则返回空集合
     */
    Set<String> getDuplicateAvatarOssPath();

    /**
     * 根据给定的文件路径获取后续相关的头像文件 OSS 路径集合。
     *
     * @param path 文件的路径，用于查询后续相关的头像文件 OSS 路径
     * @return 相关的头像文件 OSS 路径集合，若无符合条件的路径则返回空集合
     */
    Set<String> getAvatarOssPathAfterByPath(String path);

    /**
     * 根据给定的 OSS 文件路径删除对应的日志记录。
     *
     * @param ossPath OSS 文件的存储路径，标识需要删除的日志记录对应的文件路径
     * @return
     */
    int removeByOssPath(String ossPath);
}