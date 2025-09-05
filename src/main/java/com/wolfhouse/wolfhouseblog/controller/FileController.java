package com.wolfhouse.wolfhouseblog.controller;

import com.wolfhouse.wolfhouseblog.common.exceptions.ServiceException;
import com.wolfhouse.wolfhouseblog.common.http.HttpMediaTypeConstant;
import com.wolfhouse.wolfhouseblog.common.http.HttpResult;
import com.wolfhouse.wolfhouseblog.pojo.dto.file.ChunkFilePermitDto;
import com.wolfhouse.wolfhouseblog.pojo.dto.file.ClientChunkFileUploadDto;
import com.wolfhouse.wolfhouseblog.pojo.vo.file.ChunkFilePermitVo;
import com.wolfhouse.wolfhouseblog.pojo.vo.file.FileUploadResultVo;
import com.wolfhouse.wolfhouseblog.service.file.FileService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

/**
 * @author linexsong
 */
@RestController
@RequestMapping("/file")
@RequiredArgsConstructor
@Tag(name = "文件上传接口")
public class FileController {
    private final FileService service;

    @Operation(summary = "开始头像上传")
    @PostMapping("/avatar/start")
    public HttpResult<ChunkFilePermitVo> startUploadAvatar(@RequestBody ChunkFilePermitDto dto) throws Exception {
        return HttpResult.failedIfBlank(service.startUploadAvatar(dto));
    }

    @Operation(summary = "停止头像上传")
    @PostMapping("/avatar/stop/{uploadId}")
    public HttpResult<FileUploadResultVo> stopUploadAvatar(@PathVariable Long uploadId) {
        return HttpResult.failedIfBlank(service.completeUploadAvatar(uploadId));
    }

    @Operation(summary = "上传头像")
    @PostMapping(value = "/avatar",
                 consumes = HttpMediaTypeConstant.APPLICATION_OCTET_STREAM_VALUE)
    public HttpResult<FileUploadResultVo> uploadAvatar(@ModelAttribute
                                                       @Valid
                                                       ClientChunkFileUploadDto uploadDto,
                                                       HttpServletRequest request) {
        try (var ins = request.getInputStream()) {
            uploadDto.setIns(ins);
            return HttpResult.failedIfBlank(service.uploadAvatar(uploadDto));
        } catch (Exception e) {
            throw new ServiceException(e.getMessage(), e);
        }
    }
}
