package com.fresh.product.controller;

import com.fresh.common.result.R;
import com.fresh.product.dto.StockLockDTO;
import com.fresh.product.dto.StockTransferDTO;
import com.fresh.product.entity.StockLog;
import com.fresh.product.service.StockLogService;
import com.fresh.product.service.WarehouseStockService;
import com.fresh.product.vo.StockVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "库存管理", description = "库存相关接口")
@RestController
@RequestMapping("/stock")
public class StockController {

    @Resource
    private WarehouseStockService warehouseStockService;

    @Resource
    private StockLogService stockLogService;

    @Operation(summary = "查询SKU各仓库存")
    @GetMapping("/sku/{skuId}")
    public R<List<StockVO>> getStockBySkuId(@PathVariable Long skuId) {
        return R.success(warehouseStockService.getStockBySkuId(skuId));
    }

    @Operation(summary = "查询指定仓指定SKU库存")
    @GetMapping("/detail")
    public R<StockVO> getStockByWarehouseAndSku(@RequestParam Long warehouseId, @RequestParam Long skuId) {
        return R.success(warehouseStockService.getStockByWarehouseAndSku(warehouseId, skuId));
    }

    @Operation(summary = "预扣库存（下单锁定）")
    @PostMapping("/lock")
    public R<Boolean> lockStock(@Valid @RequestBody StockLockDTO dto) {
        return R.success(warehouseStockService.lockStock(dto));
    }

    @Operation(summary = "释放库存（取消/超时回滚）")
    @PostMapping("/release")
    public R<Boolean> releaseStock(@Valid @RequestBody StockLockDTO dto) {
        return R.success(warehouseStockService.releaseStock(dto));
    }

    @Operation(summary = "扣减库存（支付后真正扣减）")
    @PostMapping("/deduct")
    public R<Boolean> deductStock(@Valid @RequestBody StockLockDTO dto) {
        return R.success(warehouseStockService.deductStock(dto));
    }

    @Operation(summary = "回补库存（售后退货）")
    @PostMapping("/restore")
    public R<Boolean> restoreStock(@Valid @RequestBody StockLockDTO dto) {
        return R.success(warehouseStockService.restoreStock(dto));
    }

    @Operation(summary = "库存调拨（跨仓调拨）")
    @PostMapping("/transfer")
    public R<Boolean> transferStock(@Valid @RequestBody StockTransferDTO dto) {
        return R.success(warehouseStockService.transferStock(dto));
    }

    @Operation(summary = "库存入库")
    @PostMapping("/in")
    public R<Boolean> stockIn(@RequestParam Long warehouseId,
                              @RequestParam Long skuId,
                              @RequestParam Integer qty,
                              @RequestParam(required = false) String orderNo,
                              @RequestParam(required = false) String remark) {
        return R.success(warehouseStockService.stockIn(warehouseId, skuId, qty, orderNo, remark));
    }

    @Operation(summary = "库存出库")
    @PostMapping("/out")
    public R<Boolean> stockOut(@RequestParam Long warehouseId,
                               @RequestParam Long skuId,
                               @RequestParam Integer qty,
                               @RequestParam(required = false) String orderNo,
                               @RequestParam(required = false) String remark) {
        return R.success(warehouseStockService.stockOut(warehouseId, skuId, qty, orderNo, remark));
    }

    @Operation(summary = "初始化库存")
    @PostMapping("/init")
    public R<Void> initStock(@RequestParam Long warehouseId,
                             @RequestParam Long skuId,
                             @RequestParam Integer qty) {
        warehouseStockService.initStock(warehouseId, skuId, qty);
        return R.success();
    }

    @Operation(summary = "库存流水列表")
    @GetMapping("/log/list")
    public R<List<StockLog>> logList(@RequestParam(required = false) Long warehouseId,
                                     @RequestParam(required = false) Long skuId,
                                     @RequestParam(required = false) Integer type) {
        return R.success(stockLogService.list(warehouseId, skuId, type));
    }
}
