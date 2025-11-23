package com.wolfhouse.wolfhouseblog.mapper;

import com.mybatisflex.core.BaseMapper;
import com.wolfhouse.wolfhouseblog.common.enums.oss.FileType;
import com.wolfhouse.wolfhouseblog.pojo.domain.OssUploadLog;
import org.apache.ibatis.annotations.Mapper;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Set;

/**
 * 映射层。
 *
 * @author Rylin Wolf
 * @since 1.0
 */
@Mapper
public interface OssUploadLogMapper extends BaseMapper<OssUploadLog> {
    /**
     * 根据上传时间和文件业务类型获取符合条件的日志 ID 集合。
     *
     * @param beforeTime 上传时间的上限，获取在此时间之前上传的日志。不允许为 null。
     * @param fileType   文件业务类型，用于筛选指定类型的文件日志。不允许为 null。
     * @return 符合条件的日志 ID 集合。如果没有符合条件的数据，将返回空集合。
     */
    Set<Long> getLogIdsBy(LocalDateTime beforeTime, FileType fileType);


    /**
     * 根据提供的日志 ID 集合，获取每个上传用户的 OSS 文件路径集合，
     * 过滤掉每个用户最近上传的文件。
     *
     * @param ids 日志 ID 集合，用于匹配 OSS 上传日志的文件路径。不允许为 null 或为空。
     * @return 过滤掉每个用户最近上传文件后的 OSS 文件路径集合。如果没有符合条件的路径，则返回空集合。
     */
    Set<String> ossPathWithoutRecentOne(Collection<Long> ids);
}
