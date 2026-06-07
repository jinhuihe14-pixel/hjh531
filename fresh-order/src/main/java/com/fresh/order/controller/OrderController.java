package com.fresh.order.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.fresh.common.result.R;
import com.fresh.order.dto.OrderCreateDTO;
import com.fresh.order.dto.OrderQueryDTO;
import com.fresh.order.service.OrderService;
import com.fresh.order.vo.OrderDetailVO;
import com.fresh.order.vo.OrderVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

@Tag(name = "订单管理", description = "用户端订单接口")
@RestController
@RequestMapping("/order")
public class OrderController {

    @Resource
    private OrderService orderService;

    @Operation(summary = "创建订单")
    @PostMapping("/create")
    public R<String> createOrder(@Valid @RequestBody OrderCreateDTO dto) {
        String orderNo = orderService.createOrder(dto);
        return R.success(orderNo);
    }

    @Operation(summary = "订单列表")
    @GetMapping("/list")
    public R<Page<OrderVO>> orderList(OrderQueryDTO dto) {
        Page<OrderVO> page = orderService.orderList(dto);
        return R.success(page);
    }

    @Operation(summary = "订单详情")
    @GetMapping("/{id}")
    public R<OrderDetailVO> orderDetail(@PathVariable("id") Long id) {
        OrderDetailVO detail = orderService.orderDetail(id);
        return R.success(detail);
    }

    @Operation(summary = "取消订单")
    @PostMapping("/cancel/{id}")
    public R<Boolean> cancelOrder(@PathVariable("id") Long id) {
        Boolean result = orderService.cancelOrder(id);
        return R.success(result);
    }

    @Operation(summary = "确认收货")
    @PostMapping("/confirm/{id}")
    public R<Boolean> confirmReceive(@PathVariable("id") Long id) {
        Boolean result = orderService.confirmReceive(id);
        return R.success(result);
    }

    @Operation(summary = "支付回调")
    @PostMapping("/pay/callback")
    public R<Boolean> payCallback(@RequestParam("orderNo") String orderNo) {
        Boolean result = orderService.payCallback(orderNo);
        return R.success(result);
    }

}
