package com.fresh.delivery.task;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.fresh.delivery.entity.DeliveryOrder;
import com.fresh.delivery.enums.DeliveryOrderStatus;
import com.fresh.delivery.service.DeliveryOrderService;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Component
public class DeliveryTimeoutTask {

    @Resource
    private DeliveryOrderService deliveryOrderService;

    @Value("${delivery.timeout.warning-minutes:10}")
    private Integer warningMinutes;

    @Value("${delivery.timeout.overtime-minutes:30}")
    private Integer overtimeMinutes;

    @Value("${delivery.timeout.redispatch:true}")
    private Boolean redispatch;

    @Scheduled(fixedRate = 60000)
    public void checkTimeoutDelivery() {
        log.info("开始检查超时配送订单");

        try {
            LambdaQueryWrapper<DeliveryOrder> wrapper = new LambdaQueryWrapper<>();
            wrapper.in(DeliveryOrder::getStatus,
                    DeliveryOrderStatus.ACCEPTED.getCode(),
                    DeliveryOrderStatus.PICKING_UP.getCode(),
                    DeliveryOrderStatus.DELIVERING.getCode());
            List<DeliveryOrder> deliveringOrders = deliveryOrderService.list(wrapper);

            int warningCount = 0;
            int overtimeCount = 0;
            int redispatchCount = 0;

            for (DeliveryOrder order : deliveringOrders) {
                if (order.getPickupTime() == null && order.getCreateTime() == null) {
                    continue;
                }

                LocalDateTime startTime = order.getPickupTime() != null ? order.getPickupTime() : order.getCreateTime();
                LocalDateTime now = LocalDateTime.now();
                long minutes = java.time.Duration.between(startTime, now).toMinutes();
                int estimateTime = order.getEstimateTime() != null ? order.getEstimateTime() : 30;

                if (minutes >= estimateTime + overtimeMinutes) {
                    overtimeCount++;
                    log.warn("配送单已超时，配送单号：{}，已配送：{}分钟，预计：{}分钟",
                            order.getDeliveryNo(), minutes, estimateTime);

                    if (redispatch) {
                        try {
                            redispatchDelivery(order);
                            redispatchCount++;
                        } catch (Exception e) {
                            log.error("超时订单二次调度失败，配送单号：{}", order.getDeliveryNo(), e);
                        }
                    }
                } else if (minutes >= estimateTime - warningMinutes) {
                    warningCount++;
                    log.info("配送单即将超时，配送单号：{}，已配送：{}分钟，预计：{}分钟",
                            order.getDeliveryNo(), minutes, estimateTime);
                }
            }

            log.info("超时检查完成，即将超时：{}单，已超时：{}单，二次调度：{}单",
                    warningCount, overtimeCount, redispatchCount);

        } catch (Exception e) {
            log.error("检查超时配送订单失败", e);
        }
    }

    private void redispatchDelivery(DeliveryOrder order) {
        log.info("超时订单二次调度，配送单号：{}", order.getDeliveryNo());
        deliveryOrderService.redispatchOrder(order.getId(), "配送超时，二次调度");
    }
}
