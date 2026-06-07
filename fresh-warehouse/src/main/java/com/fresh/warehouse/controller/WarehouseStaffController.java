package com.fresh.warehouse.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.fresh.common.page.PageQuery;
import com.fresh.common.result.R;
import com.fresh.warehouse.entity.WarehouseStaff;
import com.fresh.warehouse.service.WarehouseStaffService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "仓内操作人员管理", description = "仓内操作人员相关接口")
@RestController
@RequestMapping("/staff")
public class WarehouseStaffController {

    @Resource
    private WarehouseStaffService warehouseStaffService;

    @Operation(summary = "分页查询人员列表")
    @GetMapping("/page")
    public R<IPage<WarehouseStaff>> page(PageQuery pageQuery,
                                          @RequestParam(required = false) Long warehouseId,
                                          @RequestParam(required = false) Integer role,
                                          @RequestParam(required = false) Integer status) {
        return R.success(warehouseStaffService.page(pageQuery, warehouseId, role, status));
    }

    @Operation(summary = "查询仓库人员列表")
    @GetMapping("/list")
    public R<List<WarehouseStaff>> list(@RequestParam(required = false) Long warehouseId,
                                         @RequestParam(required = false) Integer role) {
        return R.success(warehouseStaffService.listByWarehouseId(warehouseId, role));
    }

    @Operation(summary = "查询人员详情")
    @GetMapping("/{id}")
    public R<WarehouseStaff> getById(@PathVariable Long id) {
        return R.success(warehouseStaffService.getById(id));
    }

    @Operation(summary = "新增人员")
    @PostMapping
    public R<Void> add(@RequestBody WarehouseStaff staff) {
        warehouseStaffService.add(staff);
        return R.success();
    }

    @Operation(summary = "修改人员")
    @PutMapping
    public R<Void> update(@RequestBody WarehouseStaff staff) {
        warehouseStaffService.update(staff);
        return R.success();
    }

    @Operation(summary = "删除人员")
    @DeleteMapping("/{id}")
    public R<Void> delete(@PathVariable Long id) {
        warehouseStaffService.delete(id);
        return R.success();
    }

    @Operation(summary = "切换人员状态")
    @PutMapping("/status")
    public R<Void> updateStatus(@RequestParam Long id, @RequestParam Integer status) {
        warehouseStaffService.updateStatus(id, status);
        return R.success();
    }
}
