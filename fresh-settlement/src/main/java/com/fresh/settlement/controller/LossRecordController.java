package com.fresh.settlement.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.fresh.common.result.R;
import com.fresh.settlement.dto.LossRecordAddDTO;
import com.fresh.settlement.dto.LossRecordQueryDTO;
import com.fresh.settlement.service.LossRecordService;
import com.fresh.settlement.vo.LossRecordVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

@Tag(name = "损耗台账", description = "损耗记录管理接口")
@RestController
@RequestMapping("/settlement/loss")
public class LossRecordController {

    @Resource
    private LossRecordService lossRecordService;

    @Operation(summary = "新增损耗记录")
    @PostMapping("/add")
    public R<String> addLossRecord(@Valid @RequestBody LossRecordAddDTO dto) {
        String lossNo = lossRecordService.addLossRecord(dto);
        return R.success(lossNo);
    }

    @Operation(summary = "损耗记录列表")
    @GetMapping("/list")
    public R<Page<LossRecordVO>> lossRecordList(LossRecordQueryDTO dto) {
        Page<LossRecordVO> page = lossRecordService.lossRecordPage(dto);
        return R.success(page);
    }

    @Operation(summary = "损耗记录详情")
    @GetMapping("/{id}")
    public R<LossRecordVO> lossRecordDetail(@PathVariable("id") Long id) {
        LossRecordVO detail = lossRecordService.lossRecordDetail(id);
        return R.success(detail);
    }
}
