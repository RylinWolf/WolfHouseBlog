package com.wolfhouse.wolfhouseblog.service.impl;

import com.wolfhouse.wolfhouseblog.pojo.dto.file.ChunkFilePermitDto;
import com.wolfhouse.wolfhouseblog.pojo.vo.file.ChunkFilePermitVo;
import com.wolfhouse.wolfhouseblog.pojo.vo.file.FileUploadResultVo;
import com.wolfhouse.wolfhouseblog.service.FileService;
import org.springframework.stereotype.Service;

import java.io.InputStream;

/**
 * Oss 文件服务实现类
 *
 * @author linexsong
 */
@Service
public class OssFileServiceImpl implements FileService {
    @Override
    public FileUploadResultVo uploadAvatar(Long uploadId, InputStream ins) {
        return null;
    }

    @Override
    public ChunkFilePermitVo startUploadAvatar(ChunkFilePermitDto dto) {
        return null;
    }

    @Override
    public FileUploadResultVo stopUploadAvatar(Long uploadId) {
        return null;
    }
}
