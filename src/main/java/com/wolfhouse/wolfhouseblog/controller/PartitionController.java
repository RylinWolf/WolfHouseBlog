package com.wolfhouse.wolfhouseblog.controller;

import com.wolfhouse.wolfhouseblog.common.constant.services.PartitionConstant;
import com.wolfhouse.wolfhouseblog.common.http.HttpCodeConstant;
import com.wolfhouse.wolfhouseblog.common.http.HttpMediaTypeConstant;
import com.wolfhouse.wolfhouseblog.common.http.HttpResult;
import com.wolfhouse.wolfhouseblog.pojo.dto.PartitionDto;
import com.wolfhouse.wolfhouseblog.pojo.dto.PartitionUpdateDto;
import com.wolfhouse.wolfhouseblog.pojo.vo.PartitionVo;
import com.wolfhouse.wolfhouseblog.service.PartitionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.SortedSet;

/**
 * @author linexsong
 */
@RestController
@RequestMapping("/part")
@RequiredArgsConstructor
@Tag(name = "分区接口")
public class PartitionController {
    private final PartitionService service;

    @Operation(summary = "添加")
    @PostMapping
    public HttpResult<SortedSet<PartitionVo>> addPartition(@RequestBody @Valid PartitionDto dto) throws Exception {
        return HttpResult.failedIfBlank(
             HttpCodeConstant.FAILED,
             PartitionConstant.ADD_FAILED,
             service.addPartition(dto));
    }

    @Operation(summary = "获取全部分区")
    @GetMapping
    public HttpResult<SortedSet<PartitionVo>> getAllPartition() throws Exception {
        return HttpResult.success(service.getPartitionVos());
    }

    @Operation(summary = "更新")
    @PatchMapping(consumes = HttpMediaTypeConstant.APPLICATION_JSON_NULLABLE_VALUE)
    public HttpResult<SortedSet<PartitionVo>> updatePartition(@RequestBody PartitionUpdateDto dto) throws Exception {
        return HttpResult.failedIfBlank(
             HttpCodeConstant.UPDATE_FAILED,
             PartitionConstant.UPDATE_FAILED,
             service.updatePatch(dto));
    }

    @Operation(summary = "按 ID 删除")
    @DeleteMapping(value = "/{id}")
    public HttpResult<SortedSet<PartitionVo>> deletePartition(@PathVariable Long id) throws Exception {
        return HttpResult.failedIfBlank(
             HttpCodeConstant.FAILED,
             PartitionConstant.DELETE_FAILED,
             service.deleteOne(id));
    }

    @Operation(summary = "按 ID 级联删除")
    @DeleteMapping(value = "/batch/{id}")
    public HttpResult<SortedSet<PartitionVo>> deletePartitionBatch(@PathVariable Long id) throws Exception {
        return HttpResult.failedIfBlank(
             HttpCodeConstant.FAILED,
             PartitionConstant.DELETE_FAILED,
             service.deleteBatch(id));
    }

}
