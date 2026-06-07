package com.fresh.settlement.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.fresh.common.result.R;
import com.fresh.settlement.entity.Supplier;
import com.fresh.settlement.service.SupplierService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.*;

@Tag(name = "供应商管理", description = "供应商管理接口")
@RestController
@RequestMapping("/settlement/supplier/info")
public class SupplierController {

    @Resource
    private SupplierService supplierService;

    @Operation(summary = "供应商列表")
    @GetMapping("/list")
    public R<Page<Supplier>> supplierList(
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize) {
        Page<Supplier> page = supplierService.page(new Page<>(pageNum, pageSize));
        return R.success(page);
    }

    @Operation(summary = "供应商详情")
    @GetMapping("/{id}")
    public R<Supplier> supplierDetail(@PathVariable("id") Long id) {
        Supplier supplier = supplierService.getById(id);
        return R.success(supplier);
    }

    @Operation(summary = "新增供应商")
    @PostMapping("/add")
    public R<Boolean> addSupplier(@RequestBody Supplier supplier) {
        Boolean result = supplierService.save(supplier);
        return R.success(result);
    }

    @Operation(summary = "更新供应商")
    @PostMapping("/update")
    public R<Boolean> updateSupplier(@RequestBody Supplier supplier) {
        Boolean result = supplierService.updateById(supplier);
        return R.success(result);
    }

    @Operation(summary = "删除供应商")
    @PostMapping("/delete/{id}")
    public R<Boolean> deleteSupplier(@PathVariable("id") Long id) {
        Boolean result = supplierService.removeById(id);
        return R.success(result);
    }
}
