package com.wolfhouse.wolfhouseblog.service;


import com.mybatisflex.core.service.IService;
import com.wolfhouse.wolfhouseblog.pojo.domain.OssUploadLog;

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
     * @param filepath 文件存储的路径
     */
    boolean log(String filename, Long size, String filepath);
}