package com.fresh.order.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.fresh.common.result.R;
import com.fresh.order.dto.OrderQueryDTO;
import com.fresh.order.service.OrderService;
import com.fresh.order.vo.OrderVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.*;

@Tag(name = "订单管理后台", description = "后台管理订单接口")
@RestController
@RequestMapping("/admin/order")
public class OrderAdminController {

    @Resource
    private OrderService orderService;

    @Operation(summary = "订单列表")
    @GetMapping("/list")
    public R<Page<OrderVO>> orderList(OrderQueryDTO dto) {
        Page<OrderVO> page = orderService.adminOrderList(dto);
        return R.success(page);
    }

    @Operation(summary = "订单发货")
    @PostMapping("/deliver/{id}")
    public R<Boolean> deliverOrder(@PathVariable("id") Long id,
                                   @RequestParam("warehouseId") Long warehouseId,
                                   @RequestParam("riderId") Long riderId) {
        Boolean result = orderService.deliverOrder(id, warehouseId, riderId);
        return R.success(result);
    }

    @Operation(summary = "改派仓库")
    @PostMapping("/transfer/{id}")
    public R<Boolean> transferWarehouse(@PathVariable("id") Long id,
                                         @RequestParam("fromWarehouseId") Long fromWarehouseId,
                                         @RequestParam("toWarehouseId") Long toWarehouseId) {
        Boolean result = orderService.transferWarehouse(id, fromWarehouseId, toWarehouseId);
        return R.success(result);
    }

}
