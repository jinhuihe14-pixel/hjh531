package com.fresh.delivery.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.fresh.common.context.UserContext;
import com.fresh.common.result.R;
import com.fresh.delivery.entity.Rider;
import com.fresh.delivery.service.DeliveryOrderService;
import com.fresh.delivery.service.DeliveryTrackService;
import com.fresh.delivery.service.RiderService;
import com.fresh.delivery.vo.DeliveryOrderVO;
import com.fresh.delivery.vo.RiderVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;

@Tag(name = "骑手端", description = "骑手端配送接口")
@RestController
@RequestMapping("/rider/delivery")
public class RiderDeliveryController {

    @Resource
    private RiderService riderService;

    @Resource
    private DeliveryOrderService deliveryOrderService;

    @Resource
    private DeliveryTrackService deliveryTrackService;

    @Operation(summary = "骑手信息")
    @GetMapping("/info")
    public R<RiderVO> riderInfo() {
        Long riderId = UserContext.getUserId();
        RiderVO detail = riderService.riderDetail(riderId);
        return R.success(detail);
    }

    @Operation(summary = "更新工作状态")
    @PostMapping("/work-status")
    public R<Boolean> updateWorkStatus(@RequestParam("workStatus") Integer workStatus) {
        Long riderId = UserContext.getUserId();
        Boolean result = riderService.updateWorkStatus(riderId, workStatus);
        return R.success(result);
    }

    @Operation(summary = "上报位置")
    @PostMapping("/location")
    public R<Boolean> reportLocation(
            @RequestParam("longitude") BigDecimal longitude,
            @RequestParam("latitude") BigDecimal latitude,
            @RequestParam(value = "speed", required = false) BigDecimal speed,
            @RequestParam(value = "direction", required = false) String direction,
            @RequestParam(value = "deliveryId", required = false) Long deliveryId) {
        Long riderId = UserContext.getUserId();
        riderService.updateLocation(riderId, longitude, latitude);
        if (deliveryId != null) {
            deliveryTrackService.addTrack(deliveryId, riderId, longitude, latitude, speed, direction);
        }
        return R.success(true);
    }

    @Operation(summary = "接单列表")
    @GetMapping("/order/list")
    public R<Page<DeliveryOrderVO>> orderList(
            @RequestParam(value = "status", required = false) Integer status,
            @RequestParam("pageNum") Integer pageNum,
            @RequestParam("pageSize") Integer pageSize) {
        Long riderId = UserContext.getUserId();
        Page<DeliveryOrderVO> page = deliveryOrderService.riderOrderList(riderId, status, pageNum, pageSize);
        return R.success(page);
    }

    @Operation(summary = "接单")
    @PostMapping("/order/accept/{id}")
    public R<Boolean> acceptOrder(@PathVariable("id") Long id) {
        Long riderId = UserContext.getUserId();
        Boolean result = deliveryOrderService.acceptOrder(id, riderId);
        return R.success(result);
    }

    @Operation(summary = "取货确认")
    @PostMapping("/order/pickup/{id}")
    public R<Boolean> pickupConfirm(@PathVariable("id") Long id) {
        Boolean result = deliveryOrderService.pickupConfirm(id);
        return R.success(result);
    }

    @Operation(summary = "送达确认")
    @PostMapping("/order/delivery/{id}")
    public R<Boolean> deliveryConfirm(@PathVariable("id") Long id) {
        Boolean result = deliveryOrderService.deliveryConfirm(id);
        return R.success(result);
    }

}
