package com.fresh.delivery.controller;

import com.fresh.common.result.R;
import com.fresh.delivery.service.DeliveryOrderService;
import com.fresh.delivery.vo.DeliveryDetailVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.*;

@Tag(name = "用户端配送", description = "用户端配送查询接口")
@RestController
@RequestMapping("/delivery")
public class DeliveryUserController {

    @Resource
    private DeliveryOrderService deliveryOrderService;

    @Operation(summary = "配送详情")
    @GetMapping("/detail/{id}")
    public R<DeliveryDetailVO> deliveryDetail(@PathVariable("id") Long id) {
        DeliveryDetailVO detail = deliveryOrderService.deliveryDetail(id);
        return R.success(detail);
    }

    @Operation(summary = "根据订单号查询配送详情")
    @GetMapping("/detail/order/{orderNo}")
    public R<DeliveryDetailVO> deliveryDetailByOrderNo(@PathVariable("orderNo") String orderNo) {
        DeliveryDetailVO detail = deliveryOrderService.deliveryDetailByOrderNo(orderNo);
        return R.success(detail);
    }

}
