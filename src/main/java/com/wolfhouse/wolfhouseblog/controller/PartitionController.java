package com.wolfhouse.wolfhouseblog.controller;

import com.wolfhouse.wolfhouseblog.common.constant.services.PartitionConstant;
import com.wolfhouse.wolfhouseblog.common.http.HttpCodeConstant;
import com.wolfhouse.wolfhouseblog.common.http.HttpResult;
import com.wolfhouse.wolfhouseblog.pojo.dto.PartitionDto;
import com.wolfhouse.wolfhouseblog.pojo.dto.PartitionUpdateDto;
import com.wolfhouse.wolfhouseblog.pojo.vo.PartitionVo;
import com.wolfhouse.wolfhouseblog.service.PartitionService;
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
public class PartitionController {
    private final PartitionService service;

    @PostMapping
    public HttpResult<SortedSet<PartitionVo>> addPartition(@RequestBody @Valid PartitionDto dto) throws Exception {
        return HttpResult.failedIfBlank(
             HttpCodeConstant.FAILED,
             PartitionConstant.ADD_FAILED,
             service.addPartition(dto));
    }

    @GetMapping
    public HttpResult<SortedSet<PartitionVo>> getAllPartition() throws Exception {
        return HttpResult.success(service.getPartitionVos());
    }

    @PutMapping
    public HttpResult<SortedSet<PartitionVo>> updatePartition(@RequestBody PartitionUpdateDto dto) throws Exception {
        return HttpResult.failedIfBlank(
             HttpCodeConstant.UPDATE_FAILED,
             PartitionConstant.UPDATE_FAILED,
             service.updatePatch(dto));

    }

}
