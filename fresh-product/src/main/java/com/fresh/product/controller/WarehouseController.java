package com.fresh.product.controller;

import com.fresh.common.result.R;
import com.fresh.product.entity.Warehouse;
import com.fresh.product.service.WarehouseService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "前置仓管理", description = "前置仓相关接口")
@RestController
@RequestMapping("/warehouse")
public class WarehouseController {

    @Resource
    private WarehouseService warehouseService;

    @Operation(summary = "仓库列表")
    @GetMapping("/list")
    public R<List<Warehouse>> list(@RequestParam(required = false) Integer status) {
        return R.success(warehouseService.list(status));
    }

    @Operation(summary = "仓库详情")
    @GetMapping("/{id}")
    public R<Warehouse> getById(@PathVariable Long id) {
        return R.success(warehouseService.getById(id));
    }

    @Operation(summary = "新增仓库")
    @PostMapping
    public R<Void> add(@RequestBody Warehouse warehouse) {
        warehouseService.add(warehouse);
        return R.success();
    }

    @Operation(summary = "修改仓库")
    @PutMapping
    public R<Void> update(@RequestBody Warehouse warehouse) {
        warehouseService.update(warehouse);
        return R.success();
    }

    @Operation(summary = "删除仓库")
    @DeleteMapping("/{id}")
    public R<Void> delete(@PathVariable Long id) {
        warehouseService.delete(id);
        return R.success();
    }
}
