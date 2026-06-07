package com.fresh.delivery.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.fresh.common.result.R;
import com.fresh.delivery.dto.DeliveryOrderCreateDTO;
import com.fresh.delivery.dto.DeliveryOrderQueryDTO;
import com.fresh.delivery.service.DeliveryOrderService;
import com.fresh.delivery.vo.DeliveryDetailVO;
import com.fresh.delivery.vo.DeliveryOrderVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

@Tag(name = "后台配送管理", description = "后台配送管理接口")
@RestController
@RequestMapping("/admin/delivery")
public class DeliveryAdminController {

    @Resource
    private DeliveryOrderService deliveryOrderService;

    @Operation(summary = "配送单列表")
    @GetMapping("/page")
    public R<Page<DeliveryOrderVO>> deliveryOrderPage(DeliveryOrderQueryDTO dto) {
        Page<DeliveryOrderVO> page = deliveryOrderService.deliveryOrderPage(dto);
        return R.success(page);
    }

    @Operation(summary = "配送单详情")
    @GetMapping("/{id}")
    public R<DeliveryDetailVO> deliveryDetail(@PathVariable("id") Long id) {
        DeliveryDetailVO detail = deliveryOrderService.deliveryDetail(id);
        return R.success(detail);
    }

    @Operation(summary = "创建配送单")
    @PostMapping("/create")
    public R<String> createDeliveryOrder(@Valid @RequestBody DeliveryOrderCreateDTO dto) {
        String deliveryNo = deliveryOrderService.createDeliveryOrder(dto);
        return R.success(deliveryNo);
    }

    @Operation(summary = "手动派单")
    @PostMapping("/dispatch/{id}")
    public R<Boolean> manualDispatch(@PathVariable("id") Long id, @RequestParam("riderId") Long riderId) {
        Boolean result = deliveryOrderService.manualDispatch(id, riderId);
        return R.success(result);
    }

    @Operation(summary = "改派订单")
    @PostMapping("/transfer/{id}")
    public R<Boolean> transferOrder(
            @PathVariable("id") Long id,
            @RequestParam("fromRiderId") Long fromRiderId,
            @RequestParam("toRiderId") Long toRiderId,
            @RequestParam(value = "reason", required = false) String reason) {
        Boolean result = deliveryOrderService.transferOrder(id, fromRiderId, toRiderId, reason);
        return R.success(result);
    }

    @Operation(summary = "取消配送")
    @PostMapping("/cancel/{id}")
    public R<Boolean> cancelDelivery(@PathVariable("id") Long id,
                                      @RequestParam(value = "reason", required = false) String reason) {
        Boolean result = deliveryOrderService.cancelDelivery(id, reason);
        return R.success(result);
    }

}
