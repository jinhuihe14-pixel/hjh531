package com.fresh.warehouse.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.fresh.common.page.PageQuery;
import com.fresh.common.result.R;
import com.fresh.warehouse.dto.StockTransferCreateDTO;
import com.fresh.warehouse.dto.StockTransferQueryDTO;
import com.fresh.warehouse.entity.StockTransfer;
import com.fresh.warehouse.service.StockTransferService;
import com.fresh.warehouse.vo.StockTransferDetailVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

@Tag(name = "库存调拨管理", description = "库存调拨相关接口")
@RestController
@RequestMapping("/stockTransfer")
public class StockTransferController {

    @Resource
    private StockTransferService stockTransferService;

    @Operation(summary = "分页查询调拨单列表")
    @GetMapping("/page")
    public R<IPage<StockTransfer>> page(PageQuery pageQuery, StockTransferQueryDTO queryDTO) {
        return R.success(stockTransferService.page(pageQuery, queryDTO));
    }

    @Operation(summary = "查询调拨单详情")
    @GetMapping("/{id}")
    public R<StockTransferDetailVO> getDetailById(@PathVariable Long id) {
        return R.success(stockTransferService.getDetailById(id));
    }

    @Operation(summary = "创建调拨申请")
    @PostMapping
    public R<Void> createTransfer(@Valid @RequestBody StockTransferCreateDTO dto) {
        stockTransferService.createTransfer(dto);
        return R.success();
    }

    @Operation(summary = "审批调拨单")
    @PutMapping("/approve")
    public R<Void> approve(@RequestParam Long id,
                           @RequestParam Boolean pass,
                           @RequestParam(required = false) String remark) {
        stockTransferService.approve(id, pass, remark);
        return R.success();
    }

    @Operation(summary = "调拨出库（确认发货）")
    @PutMapping("/outbound")
    public R<Void> outbound(@RequestParam Long id) {
        stockTransferService.outbound(id);
        return R.success();
    }

    @Operation(summary = "调拨入库（确认收货）")
    @PutMapping("/inbound")
    public R<Void> inbound(@RequestParam Long id) {
        stockTransferService.inbound(id);
        return R.success();
    }
}
