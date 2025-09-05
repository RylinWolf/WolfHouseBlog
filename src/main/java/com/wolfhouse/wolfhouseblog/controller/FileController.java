package com.wolfhouse.wolfhouseblog.controller;

import com.wolfhouse.wolfhouseblog.common.exceptions.ServiceException;
import com.wolfhouse.wolfhouseblog.common.http.HttpResult;
import com.wolfhouse.wolfhouseblog.pojo.dto.file.ChunkFilePermitDto;
import com.wolfhouse.wolfhouseblog.pojo.vo.file.ChunkFilePermitVo;
import com.wolfhouse.wolfhouseblog.pojo.vo.file.FileUploadResultVo;
import com.wolfhouse.wolfhouseblog.service.FileService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

/**
 * @author linexsong
 */
@RestController
@RequestMapping("/file")
@RequiredArgsConstructor
@Tag(name = "文件上传接口")
public class FileController {
    private final FileService service;

    @PostMapping("/avatar/start")
    @Operation(summary = "开始头像上传")
    public HttpResult<ChunkFilePermitVo> startUploadAvatar(@RequestBody ChunkFilePermitDto dto) {
        return HttpResult.failedIfBlank(service.startUploadAvatar(dto));
    }

    @PostMapping("/avatar/stop/{uploadId}")
    public HttpResult<FileUploadResultVo> stopUploadAvatar(@PathVariable Long uploadId) {
        return HttpResult.failedIfBlank(service.stopUploadAvatar(uploadId));
    }

    @PostMapping("/avatar/{uploadId}")
    public HttpResult<FileUploadResultVo> uploadAvatar(@PathVariable Long uploadId, HttpServletRequest request) {
        try (var ins = request.getInputStream()) {
            return HttpResult.failedIfBlank(service.uploadAvatar(uploadId, ins));
        } catch (IOException e) {
            throw new ServiceException(e.getMessage(), e);
        }
    }
}
