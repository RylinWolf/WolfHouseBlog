package com.wolfhouse.wolfhouseblog.service.impl;


import com.mybatisflex.core.query.QueryWrapper;
import com.mybatisflex.spring.service.impl.ServiceImpl;
import com.wolfhouse.wolfhouseblog.common.constant.redis.UserRedisConstant;
import com.wolfhouse.wolfhouseblog.common.enums.oss.FileType;
import com.wolfhouse.wolfhouseblog.common.utils.ServiceUtil;
import com.wolfhouse.wolfhouseblog.mapper.OssUploadLogMapper;
import com.wolfhouse.wolfhouseblog.pojo.domain.OssUploadLog;
import com.wolfhouse.wolfhouseblog.service.OssUploadLogService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import static com.wolfhouse.wolfhouseblog.pojo.domain.table.OssUploadLogTableDef.OSS_UPLOAD_LOG;

/**
 * 服务层实现。
 *
 * @author Rylin Wolf
 * @since 1.0
 */
@Slf4j
@Service
public class OssUploadLogServiceImpl extends ServiceImpl<OssUploadLogMapper, OssUploadLog> implements OssUploadLogService {
    @Override
    public boolean log(String filename, Long size, String filepath, String ossPath, FileType fileType) {
        Long id = ServiceUtil.loginUserOrE();
        return save(OssUploadLog.builder()
                                .filename(filename)
                                .fileSize(size)
                                .filePath(filepath)
                                .fileOssPath(ossPath)
                                .uploadUser(id)
                                .fileType(fileType)
                                .build());
    }

    @Override
    public Set<String> getUndeletedAvatarOssPath(Long userId) {
        // 每次更新头像时都应调用，所以这里不用做分页
        List<OssUploadLog> ossUploadLogs = mapper.selectListByQuery(QueryWrapper.create()
                                                                                .from(OSS_UPLOAD_LOG)
                                                                                .select(OSS_UPLOAD_LOG.FILE_OSS_PATH)
                                                                                .where(OSS_UPLOAD_LOG.UPLOAD_USER.eq(
                                                                                    userId)));
        return ossUploadLogs.stream()
                            .filter(log -> log.getFileType() == FileType.AVATAR)
                            .map(OssUploadLog::getFileOssPath)
                            .filter(Objects::nonNull)
                            .collect(Collectors.toSet());
    }

    @Override
    public Set<String> getDuplicateAvatarOssPath() {
        // 1. 根据日期、文件类型获取 ID
        LocalDateTime before = LocalDateTime.now()
                                            .minus(UserRedisConstant.AVATAR_DURATION);
        Set<Long> logIds = mapper.getLogIdsBy(before, FileType.AVATAR);
        // 2. 剔除最近一次的 ID
        return mapper.ossPathWithoutRecentOne(logIds);
    }

    @Override
    public Set<String> getAvatarOssPathAfterByPath(String path) {
        // 1. 获取到记录的时间
        OssUploadLog log = mapper.selectOneByQuery(QueryWrapper.create()
                                                               .from(OSS_UPLOAD_LOG)
                                                               .where(OSS_UPLOAD_LOG.FILE_PATH.eq(path)));
        // 2. 根据时间获取后续记录
        LocalDateTime uploadTime = log.getUploadTime();
        return mapper.selectListByQuery(QueryWrapper.create()
                                                    .from(OSS_UPLOAD_LOG)
                                                    .select(OSS_UPLOAD_LOG.FILE_OSS_PATH)
                                                    .where(OSS_UPLOAD_LOG.UPLOAD_TIME.gt(
                                                        uploadTime))
                                                    .and(OSS_UPLOAD_LOG.FILE_TYPE.eq(
                                                        FileType.AVATAR)))
                     .stream()
                     .map(OssUploadLog::getFileOssPath)
                     .collect(Collectors.toSet());
    }

    @Override
    public int removeByOssPath(String ossPath) {
        return mapper.deleteByQuery(QueryWrapper.create()
                                                .from(OSS_UPLOAD_LOG)
                                                .where(OSS_UPLOAD_LOG.FILE_OSS_PATH.eq(ossPath)));
    }
}