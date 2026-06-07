package com.fresh.delivery.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.fresh.common.result.R;
import com.fresh.delivery.dto.RiderAddDTO;
import com.fresh.delivery.dto.RiderQueryDTO;
import com.fresh.delivery.dto.RiderUpdateDTO;
import com.fresh.delivery.service.RiderService;
import com.fresh.delivery.vo.NearbyRiderVO;
import com.fresh.delivery.vo.RiderVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

@Tag(name = "骑手管理", description = "后台骑手管理接口")
@RestController
@RequestMapping("/admin/rider")
public class RiderAdminController {

    @Resource
    private RiderService riderService;

    @Operation(summary = "骑手列表")
    @GetMapping("/page")
    public R<Page<RiderVO>> riderPage(RiderQueryDTO dto) {
        Page<RiderVO> page = riderService.riderPage(dto);
        return R.success(page);
    }

    @Operation(summary = "骑手详情")
    @GetMapping("/{id}")
    public R<RiderVO> riderDetail(@PathVariable("id") Long id) {
        RiderVO detail = riderService.riderDetail(id);
        return R.success(detail);
    }

    @Operation(summary = "新增骑手")
    @PostMapping("/add")
    public R<Boolean> addRider(@Valid @RequestBody RiderAddDTO dto) {
        Boolean result = riderService.addRider(dto);
        return R.success(result);
    }

    @Operation(summary = "修改骑手")
    @PostMapping("/update/{id}")
    public R<Boolean> updateRider(@PathVariable("id") Long id, @RequestBody RiderUpdateDTO dto) {
        Boolean result = riderService.updateRider(id, dto);
        return R.success(result);
    }

    @Operation(summary = "删除骑手")
    @PostMapping("/delete/{id}")
    public R<Boolean> deleteRider(@PathVariable("id") Long id) {
        Boolean result = riderService.deleteRider(id);
        return R.success(result);
    }

    @Operation(summary = "更新骑手状态")
    @PostMapping("/status/{id}")
    public R<Boolean> updateStatus(@PathVariable("id") Long id, @RequestParam("status") Integer status) {
        Boolean result = riderService.updateStatus(id, status);
        return R.success(result);
    }

    @Operation(summary = "附近骑手查询")
    @GetMapping("/nearby")
    public R<List<NearbyRiderVO>> nearbyRiders(
            @RequestParam("longitude") BigDecimal longitude,
            @RequestParam("latitude") BigDecimal latitude,
            @RequestParam(value = "radius", required = false) Integer radius,
            @RequestParam(value = "warehouseId", required = false) Long warehouseId) {
        List<NearbyRiderVO> riders = riderService.nearbyRiders(longitude, latitude, radius, warehouseId);
        return R.success(riders);
    }

}
