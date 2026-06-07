package com.fresh.delivery.strategy;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.fresh.delivery.entity.DeliveryOrder;
import com.fresh.delivery.entity.Rider;
import com.fresh.delivery.enums.DeliveryOrderStatus;
import com.fresh.delivery.enums.RiderStatus;
import com.fresh.delivery.enums.RiderWorkStatus;
import com.fresh.delivery.mapper.DeliveryOrderMapper;
import com.fresh.delivery.mapper.RiderMapper;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@Slf4j
@Component
@ConditionalOnProperty(name = "delivery.dispatch.strategy", havingValue = "rating-first")
public class RatingFirstDispatchStrategy implements DispatchStrategy {

    @Resource
    private RiderMapper riderMapper;

    @Resource
    private DeliveryOrderMapper deliveryOrderMapper;

    @Value("${delivery.dispatch.max-rider-orders:10}")
    private Integer maxRiderOrders;

    @Value("${delivery.dispatch.search-radius:5000}")
    private Integer searchRadius;

    @Override
    public Rider dispatch(DeliveryOrder deliveryOrder) {
        log.info("使用评分优先策略派单，配送单号：{}", deliveryOrder.getDeliveryNo());

        LambdaQueryWrapper<Rider> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Rider::getStatus, RiderStatus.ON_JOB.getCode());
        wrapper.eq(Rider::getWorkStatus, RiderWorkStatus.IDLE.getCode());
        if (deliveryOrder.getWarehouseId() != null) {
            wrapper.eq(Rider::getCurrentWarehouseId, deliveryOrder.getWarehouseId());
        }
        wrapper.isNotNull(Rider::getCurrentLongitude);
        wrapper.isNotNull(Rider::getCurrentLatitude);

        List<Rider> riders = riderMapper.selectList(wrapper);
        if (riders.isEmpty()) {
            log.warn("没有可用的骑手，配送单号：{}", deliveryOrder.getDeliveryNo());
            return null;
        }

        List<RiderWithScore> riderWithScores = new ArrayList<>();
        for (Rider rider : riders) {
            int currentOrderCount = getRiderCurrentOrderCount(rider.getId());
            if (currentOrderCount >= maxRiderOrders) {
                continue;
            }

            double distance = calculateDistance(
                    deliveryOrder.getLongitude().doubleValue(),
                    deliveryOrder.getLatitude().doubleValue(),
                    rider.getCurrentLongitude().doubleValue(),
                    rider.getCurrentLatitude().doubleValue()
            );

            if (distance <= searchRadius) {
                double rating = rider.getRating() != null ? rider.getRating().doubleValue() : 5.0;
                double score = rating * 1000 - distance / 100 - currentOrderCount * 50;
                riderWithScores.add(new RiderWithScore(rider, distance, currentOrderCount, rating, score));
            }
        }

        if (riderWithScores.isEmpty()) {
            log.warn("搜索半径内没有可用骑手，配送单号：{}", deliveryOrder.getDeliveryNo());
            return null;
        }

        riderWithScores.sort(Comparator.comparingDouble(RiderWithScore::getScore).reversed());

        Rider bestRider = riderWithScores.get(0).getRider();
        log.info("评分优先策略派单成功，骑手：{}，评分：{}，距离：{}米",
                bestRider.getName(), riderWithScores.get(0).getRating(), riderWithScores.get(0).getDistance());

        return bestRider;
    }

    @Override
    public String getStrategyName() {
        return "rating-first";
    }

    private int getRiderCurrentOrderCount(Long riderId) {
        LambdaQueryWrapper<DeliveryOrder> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(DeliveryOrder::getRiderId, riderId);
        wrapper.in(DeliveryOrder::getStatus,
                DeliveryOrderStatus.ACCEPTED.getCode(),
                DeliveryOrderStatus.PICKING_UP.getCode(),
                DeliveryOrderStatus.DELIVERING.getCode());
        return Math.toIntExact(deliveryOrderMapper.selectCount(wrapper));
    }

    private double calculateDistance(double lon1, double lat1, double lon2, double lat2) {
        double earthRadius = 6371000;
        double dLat = Math.toRadians(lat2 - lat1);
        double dLon = Math.toRadians(lon2 - lon1);
        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2)
                + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
                * Math.sin(dLon / 2) * Math.sin(dLon / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        return earthRadius * c;
    }

    private static class RiderWithScore {
        private final Rider rider;
        private final double distance;
        private final int orderCount;
        private final double rating;
        private final double score;

        public RiderWithScore(Rider rider, double distance, int orderCount, double rating, double score) {
            this.rider = rider;
            this.distance = distance;
            this.orderCount = orderCount;
            this.rating = rating;
            this.score = score;
        }

        public Rider getRider() {
            return rider;
        }

        public double getDistance() {
            return distance;
        }

        public int getOrderCount() {
            return orderCount;
        }

        public double getRating() {
            return rating;
        }

        public double getScore() {
            return score;
        }
    }
}
