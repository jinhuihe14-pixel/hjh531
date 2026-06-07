package com.fresh.settlement.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.fresh.common.result.R;
import com.fresh.settlement.dto.PickerSettlementQueryDTO;
import com.fresh.settlement.service.PickerSettlementService;
import com.fresh.settlement.vo.PickerSettlementVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

@Tag(name = "分拣员结算", description = "分拣员结算管理接口")
@RestController
@RequestMapping("/settlement/picker")
public class PickerSettlementController {

    @Resource
    private PickerSettlementService pickerSettlementService;

    @Operation(summary = "生成结算单")
    @PostMapping("/generate")
    public R<String> generateSettlement(
            @RequestParam Long pickerId,
            @RequestParam LocalDateTime periodStart,
            @RequestParam LocalDateTime periodEnd) {
        String settlementNo = pickerSettlementService.generateSettlement(pickerId, periodStart, periodEnd);
        return R.success(settlementNo);
    }

    @Operation(summary = "结算单列表")
    @GetMapping("/list")
    public R<Page<PickerSettlementVO>> settlementList(PickerSettlementQueryDTO dto) {
        Page<PickerSettlementVO> page = pickerSettlementService.settlementPage(dto);
        return R.success(page);
    }

    @Operation(summary = "结算单详情")
    @GetMapping("/{id}")
    public R<PickerSettlementVO> settlementDetail(@PathVariable("id") Long id) {
        PickerSettlementVO detail = pickerSettlementService.settlementDetail(id);
        return R.success(detail);
    }

    @Operation(summary = "确认结算")
    @PostMapping("/confirm/{id}")
    public R<Boolean> confirmSettlement(@PathVariable("id") Long id) {
        Boolean result = pickerSettlementService.confirmSettlement(id);
        return R.success(result);
    }

    @Operation(summary = "付款记录")
    @PostMapping("/pay/{id}")
    public R<Boolean> paySettlement(@PathVariable("id") Long id) {
        Boolean result = pickerSettlementService.paySettlement(id);
        return R.success(result);
    }
}
