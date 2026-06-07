package com.fresh.settlement.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.fresh.common.result.R;
import com.fresh.settlement.dto.RiderSettlementQueryDTO;
import com.fresh.settlement.service.RiderSettlementService;
import com.fresh.settlement.vo.RiderSettlementVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

@Tag(name = "骑手结算", description = "骑手结算管理接口")
@RestController
@RequestMapping("/settlement/rider")
public class RiderSettlementController {

    @Resource
    private RiderSettlementService riderSettlementService;

    @Operation(summary = "生成结算单")
    @PostMapping("/generate")
    public R<String> generateSettlement(
            @RequestParam Long riderId,
            @RequestParam LocalDateTime periodStart,
            @RequestParam LocalDateTime periodEnd) {
        String settlementNo = riderSettlementService.generateSettlement(riderId, periodStart, periodEnd);
        return R.success(settlementNo);
    }

    @Operation(summary = "结算单列表")
    @GetMapping("/list")
    public R<Page<RiderSettlementVO>> settlementList(RiderSettlementQueryDTO dto) {
        Page<RiderSettlementVO> page = riderSettlementService.settlementPage(dto);
        return R.success(page);
    }

    @Operation(summary = "结算单详情")
    @GetMapping("/{id}")
    public R<RiderSettlementVO> settlementDetail(@PathVariable("id") Long id) {
        RiderSettlementVO detail = riderSettlementService.settlementDetail(id);
        return R.success(detail);
    }

    @Operation(summary = "确认结算")
    @PostMapping("/confirm/{id}")
    public R<Boolean> confirmSettlement(@PathVariable("id") Long id) {
        Boolean result = riderSettlementService.confirmSettlement(id);
        return R.success(result);
    }

    @Operation(summary = "付款记录")
    @PostMapping("/pay/{id}")
    public R<Boolean> paySettlement(@PathVariable("id") Long id) {
        Boolean result = riderSettlementService.paySettlement(id);
        return R.success(result);
    }
}
