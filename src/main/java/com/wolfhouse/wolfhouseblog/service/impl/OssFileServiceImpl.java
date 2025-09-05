package com.wolfhouse.wolfhouseblog.service.impl;

import com.wolfhouse.wolfhouseblog.auth.service.ServiceAuthMediator;
import com.wolfhouse.wolfhouseblog.common.constant.services.FileUploadConstant;
import com.wolfhouse.wolfhouseblog.common.exceptions.ServiceException;
import com.wolfhouse.wolfhouseblog.common.properties.FileUploadProperties;
import com.wolfhouse.wolfhouseblog.common.utils.BeanUtil;
import com.wolfhouse.wolfhouseblog.common.utils.OssUtil;
import com.wolfhouse.wolfhouseblog.pojo.domain.OssChunkFile;
import com.wolfhouse.wolfhouseblog.pojo.dto.file.ChunkFilePermitDto;
import com.wolfhouse.wolfhouseblog.pojo.dto.file.ClientChunkFileUploadDto;
import com.wolfhouse.wolfhouseblog.pojo.vo.file.ChunkFilePermitVo;
import com.wolfhouse.wolfhouseblog.pojo.vo.file.FileUploadResultVo;
import com.wolfhouse.wolfhouseblog.service.FileService;
import com.wolfhouse.wolfhouseblog.service.OssChunkFileService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.io.InputStream;
import java.nio.file.Path;

/**
 * Oss 文件服务实现类
 *
 * @author linexsong
 */
@Service
@RequiredArgsConstructor
public class OssFileServiceImpl implements FileService {
    private final OssUtil ossUtil;
    private final FileUploadProperties properties;
    private final ServiceAuthMediator mediator;
    private final OssChunkFileService ossChunkFileService;

    @Override
    public FileUploadResultVo uploadAvatar(Long uploadId, InputStream ins) {
        return null;
    }

    @Override
    public ChunkFilePermitVo startUploadAvatar(ChunkFilePermitDto dto) {
        // 初始化文件提交
        ChunkFilePermitVo permitVo = ossUtil
            .initialChunkUpload(ClientChunkFileUploadDto
                                    .builder()
                                    // objectName 为 头像存储地址 + 头像文件名
                                    .objectName(Path.of(properties.avatar(),
                                                        dto.getFilename())
                                                    .toString())
                                    .build());

        // 获取上传 ID
        String uploadId = permitVo.getUploadId();
        if (BeanUtil.isBlank(uploadId)) {
            throw new ServiceException(FileUploadConstant.INIT_FAILED);
        }

        // 暂存文件信息到数据库
        var chunkFile = BeanUtil.copyProperties(dto, OssChunkFile.class);
        chunkFile.setUploadId(uploadId);
        if (!ossChunkFileService.save(chunkFile)) {
            throw new ServiceException(FileUploadConstant.FILE_META_SAVE_FAILED);
        }

        return new ChunkFilePermitVo(uploadId, permitVo.getStatusCode());
    }

    @Override
    public FileUploadResultVo completeUploadAvatar(Long uploadId) {
        return null;
    }

    @Override
    public FileUploadResultVo abortUploadAvatar(Long uploadId) {
        return null;
    }
}
