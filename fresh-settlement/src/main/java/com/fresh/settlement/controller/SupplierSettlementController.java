package com.fresh.settlement.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.fresh.common.result.R;
import com.fresh.settlement.dto.SupplierSettlementGenerateDTO;
import com.fresh.settlement.dto.SupplierSettlementQueryDTO;
import com.fresh.settlement.service.SupplierSettlementService;
import com.fresh.settlement.vo.SupplierSettlementVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

@Tag(name = "供应商结算", description = "供应商结算管理接口")
@RestController
@RequestMapping("/settlement/supplier")
public class SupplierSettlementController {

    @Resource
    private SupplierSettlementService supplierSettlementService;

    @Operation(summary = "生成结算单")
    @PostMapping("/generate")
    public R<String> generateSettlement(@Valid @RequestBody SupplierSettlementGenerateDTO dto) {
        String settlementNo = supplierSettlementService.generateSettlement(dto);
        return R.success(settlementNo);
    }

    @Operation(summary = "结算单列表")
    @GetMapping("/list")
    public R<Page<SupplierSettlementVO>> settlementList(SupplierSettlementQueryDTO dto) {
        Page<SupplierSettlementVO> page = supplierSettlementService.settlementPage(dto);
        return R.success(page);
    }

    @Operation(summary = "结算单详情")
    @GetMapping("/{id}")
    public R<SupplierSettlementVO> settlementDetail(@PathVariable("id") Long id) {
        SupplierSettlementVO detail = supplierSettlementService.settlementDetail(id);
        return R.success(detail);
    }

    @Operation(summary = "确认结算")
    @PostMapping("/confirm/{id}")
    public R<Boolean> confirmSettlement(@PathVariable("id") Long id) {
        Boolean result = supplierSettlementService.confirmSettlement(id);
        return R.success(result);
    }

    @Operation(summary = "付款记录")
    @PostMapping("/pay/{id}")
    public R<Boolean> paySettlement(@PathVariable("id") Long id) {
        Boolean result = supplierSettlementService.paySettlement(id);
        return R.success(result);
    }
}
