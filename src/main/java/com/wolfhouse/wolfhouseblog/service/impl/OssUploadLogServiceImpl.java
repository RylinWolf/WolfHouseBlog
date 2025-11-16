package com.wolfhouse.wolfhouseblog.service.impl;


import com.mybatisflex.spring.service.impl.ServiceImpl;
import com.wolfhouse.wolfhouseblog.common.utils.ServiceUtil;
import com.wolfhouse.wolfhouseblog.mapper.OssUploadLogMapper;
import com.wolfhouse.wolfhouseblog.pojo.domain.OssUploadLog;
import com.wolfhouse.wolfhouseblog.service.OssUploadLogService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

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
    public boolean log(String filename, Long size, String filepath) {
        Long login = ServiceUtil.loginUserOrE();
        return save(OssUploadLog.builder()
                                .filename(filename)
                                .fileSize(size)
                                .filePath(filepath)
                                .uploadUser(login)
                                .build());
    }
}