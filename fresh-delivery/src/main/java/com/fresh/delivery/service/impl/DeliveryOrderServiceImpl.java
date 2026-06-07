package com.fresh.delivery.service.impl;

import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.fresh.common.exception.BusinessException;
import com.fresh.common.result.ResultCode;
import com.fresh.common.util.RedisUtil;
import com.fresh.delivery.dto.DeliveryOrderCreateDTO;
import com.fresh.delivery.dto.DeliveryOrderQueryDTO;
import com.fresh.delivery.entity.DeliveryOrder;
import com.fresh.delivery.entity.DeliveryTrack;
import com.fresh.delivery.entity.DispatchRecord;
import com.fresh.delivery.entity.Rider;
import com.fresh.delivery.enums.DeliveryOrderStatus;
import com.fresh.delivery.enums.RiderStatus;
import com.fresh.delivery.enums.RiderWorkStatus;
import com.fresh.delivery.mapper.DeliveryOrderMapper;
import com.fresh.delivery.service.*;
import com.fresh.delivery.strategy.DispatchStrategy;
import com.fresh.delivery.strategy.DispatchStrategyFactory;
import com.fresh.delivery.vo.DeliveryDetailVO;
import com.fresh.delivery.vo.DeliveryOrderVO;
import com.fresh.delivery.vo.DeliveryTrackVO;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.stream.function.StreamBridge;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
public class DeliveryOrderServiceImpl extends ServiceImpl<DeliveryOrderMapper, DeliveryOrder> implements DeliveryOrderService {

    @Resource
    private DeliveryTrackService deliveryTrackService;

    @Resource
    private RiderService riderService;

    @Resource
    private DispatchRecordService dispatchRecordService;

    @Resource
    private DispatchStrategyFactory dispatchStrategyFactory;

    @Resource
    private StreamBridge streamBridge;

    @Resource
    private RedisUtil redisUtil;

    @Value("${delivery.dispatch.auto-dispatch:true}")
    private Boolean autoDispatch;

    private static final String DELIVERY_NO_PREFIX = "PS";
    private static final String DELIVERY_LOCK_PREFIX = "delivery:lock:";

    @Override
    @Transactional(rollbackFor = Exception.class)
    public String createDeliveryOrder(DeliveryOrderCreateDTO dto) {
        String deliveryNo = generateDeliveryNo();

        DeliveryOrder deliveryOrder = new DeliveryOrder();
        BeanUtils.copyProperties(dto, deliveryOrder);
        deliveryOrder.setDeliveryNo(deliveryNo);
        deliveryOrder.setStatus(DeliveryOrderStatus.PENDING_ACCEPT.getCode());

        BigDecimal distance = calculateDistance(
                dto.getLongitude().doubleValue(), dto.getLatitude().doubleValue(),
                0, 0
        );
        deliveryOrder.setDistance(BigDecimal.ZERO);
        deliveryOrder.setEstimateTime(calculateEstimateTime(distance));

        if (dto.getDeliveryFee() == null) {
            deliveryOrder.setDeliveryFee(calculateDeliveryFee(distance));
        }

        this.save(deliveryOrder);

        if (autoDispatch) {
            try {
                autoDispatch(deliveryOrder);
            } catch (Exception e) {
                log.error("自动派单失败，配送单号：{}", deliveryNo, e);
            }
        }

        return deliveryNo;
    }

    @Override
    public Page<DeliveryOrderVO> deliveryOrderPage(DeliveryOrderQueryDTO dto) {
        LambdaQueryWrapper<DeliveryOrder> wrapper = buildQueryWrapper(dto);
        Page<DeliveryOrder> page = this.page(new Page<>(dto.getPageNum(), dto.getPageSize()), wrapper);
        Page<DeliveryOrderVO> result = new Page<>(page.getCurrent(), page.getSize(), page.getTotal());
        result.setRecords(page.getRecords().stream().map(this::convertToVO).toList());
        return result;
    }

    @Override
    public DeliveryDetailVO deliveryDetail(Long id) {
        DeliveryOrder deliveryOrder = this.getById(id);
        if (deliveryOrder == null) {
            throw new BusinessException(ResultCode.DATA_NOT_EXIST);
        }
        return buildDetailVO(deliveryOrder);
    }

    @Override
    public DeliveryDetailVO deliveryDetailByOrderNo(String orderNo) {
        LambdaQueryWrapper<DeliveryOrder> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(DeliveryOrder::getOrderNo, orderNo);
        wrapper.orderByDesc(DeliveryOrder::getCreateTime);
        wrapper.last("LIMIT 1");
        DeliveryOrder deliveryOrder = this.getOne(wrapper);
        if (deliveryOrder == null) {
            throw new BusinessException(ResultCode.DATA_NOT_EXIST);
        }
        return buildDetailVO(deliveryOrder);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean acceptOrder(Long id, Long riderId) {
        DeliveryOrder deliveryOrder = this.getById(id);
        if (deliveryOrder == null) {
            throw new BusinessException(ResultCode.DATA_NOT_EXIST);
        }
        if (!DeliveryOrderStatus.PENDING_ACCEPT.getCode().equals(deliveryOrder.getStatus())) {
            throw new BusinessException("配送单状态不支持接单");
        }

        Rider rider = riderService.getById(riderId);
        if (rider == null) {
            throw new BusinessException("骑手不存在");
        }
        if (!RiderStatus.ON_JOB.getCode().equals(rider.getStatus())) {
            throw new BusinessException("骑手当前状态不可接单");
        }
        if (!RiderWorkStatus.IDLE.getCode().equals(rider.getWorkStatus())) {
            throw new BusinessException("骑手当前工作状态不可接单");
        }

        deliveryOrder.setRiderId(riderId);
        deliveryOrder.setStatus(DeliveryOrderStatus.ACCEPTED.getCode());
        this.updateById(deliveryOrder);

        riderService.updateWorkStatus(riderId, RiderWorkStatus.DELIVERING.getCode());

        dispatchRecordService.addDispatchRecord(
                id, deliveryOrder.getDeliveryNo(), riderId, rider.getName(),
                2, "骑手主动接单", deliveryOrder.getDistance(), deliveryOrder.getEstimateTime()
        );

        return true;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean pickupConfirm(Long id) {
        DeliveryOrder deliveryOrder = this.getById(id);
        if (deliveryOrder == null) {
            throw new BusinessException(ResultCode.DATA_NOT_EXIST);
        }
        if (!DeliveryOrderStatus.ACCEPTED.getCode().equals(deliveryOrder.getStatus())
                && !DeliveryOrderStatus.PICKING_UP.getCode().equals(deliveryOrder.getStatus())) {
            throw new BusinessException("配送单状态不支持取货确认");
        }

        deliveryOrder.setStatus(DeliveryOrderStatus.PICKING_UP.getCode());
        deliveryOrder.setPickupTime(LocalDateTime.now());
        this.updateById(deliveryOrder);

        return true;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean deliveryConfirm(Long id) {
        DeliveryOrder deliveryOrder = this.getById(id);
        if (deliveryOrder == null) {
            throw new BusinessException(ResultCode.DATA_NOT_EXIST);
        }
        if (!DeliveryOrderStatus.DELIVERING.getCode().equals(deliveryOrder.getStatus())
                && !DeliveryOrderStatus.PICKING_UP.getCode().equals(deliveryOrder.getStatus())) {
            throw new BusinessException("配送单状态不支持送达确认");
        }

        deliveryOrder.setStatus(DeliveryOrderStatus.COMPLETED.getCode());
        deliveryOrder.setDeliveryTime(LocalDateTime.now());
        this.updateById(deliveryOrder);

        if (deliveryOrder.getRiderId() != null) {
            Rider rider = riderService.getById(deliveryOrder.getRiderId());
            if (rider != null) {
                rider.setTotalOrders(rider.getTotalOrders() + 1);
                riderService.updateById(rider);

                LambdaQueryWrapper<DeliveryOrder> wrapper = new LambdaQueryWrapper<>();
                wrapper.eq(DeliveryOrder::getRiderId, deliveryOrder.getRiderId());
                wrapper.in(DeliveryOrder::getStatus,
                        DeliveryOrderStatus.ACCEPTED.getCode(),
                        DeliveryOrderStatus.PICKING_UP.getCode(),
                        DeliveryOrderStatus.DELIVERING.getCode());
                long deliveringCount = this.count(wrapper);
                if (deliveringCount <= 0) {
                    riderService.updateWorkStatus(deliveryOrder.getRiderId(), RiderWorkStatus.IDLE.getCode());
                }
            }
        }

        return true;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean cancelDelivery(Long id, String reason) {
        DeliveryOrder deliveryOrder = this.getById(id);
        if (deliveryOrder == null) {
            throw new BusinessException(ResultCode.DATA_NOT_EXIST);
        }
        if (DeliveryOrderStatus.COMPLETED.getCode().equals(deliveryOrder.getStatus())
                || DeliveryOrderStatus.CANCELLED.getCode().equals(deliveryOrder.getStatus())) {
            throw new BusinessException("配送单状态不支持取消");
        }

        Long riderId = deliveryOrder.getRiderId();

        deliveryOrder.setStatus(DeliveryOrderStatus.CANCELLED.getCode());
        deliveryOrder.setCancelReason(reason);
        this.updateById(deliveryOrder);

        if (riderId != null) {
            LambdaQueryWrapper<DeliveryOrder> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(DeliveryOrder::getRiderId, riderId);
            wrapper.in(DeliveryOrder::getStatus,
                    DeliveryOrderStatus.ACCEPTED.getCode(),
                    DeliveryOrderStatus.PICKING_UP.getCode(),
                    DeliveryOrderStatus.DELIVERING.getCode());
            long deliveringCount = this.count(wrapper);
            if (deliveringCount <= 0) {
                riderService.updateWorkStatus(riderId, RiderWorkStatus.IDLE.getCode());
            }
        }

        return true;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean manualDispatch(Long id, Long riderId) {
        DeliveryOrder deliveryOrder = this.getById(id);
        if (deliveryOrder == null) {
            throw new BusinessException(ResultCode.DATA_NOT_EXIST);
        }
        if (!DeliveryOrderStatus.PENDING_ACCEPT.getCode().equals(deliveryOrder.getStatus())
                && !DeliveryOrderStatus.ACCEPTED.getCode().equals(deliveryOrder.getStatus())) {
            throw new BusinessException("配送单状态不支持派单");
        }

        Rider rider = riderService.getById(riderId);
        if (rider == null) {
            throw new BusinessException("骑手不存在");
        }
        if (!RiderStatus.ON_JOB.getCode().equals(rider.getStatus())) {
            throw new BusinessException("骑手当前状态不可接单");
        }

        Long oldRiderId = deliveryOrder.getRiderId();

        deliveryOrder.setRiderId(riderId);
        deliveryOrder.setStatus(DeliveryOrderStatus.ACCEPTED.getCode());
        this.updateById(deliveryOrder);

        if (RiderWorkStatus.IDLE.getCode().equals(rider.getWorkStatus())) {
            riderService.updateWorkStatus(riderId, RiderWorkStatus.DELIVERING.getCode());
        }

        if (oldRiderId != null && !oldRiderId.equals(riderId)) {
            LambdaQueryWrapper<DeliveryOrder> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(DeliveryOrder::getRiderId, oldRiderId);
            wrapper.in(DeliveryOrder::getStatus,
                    DeliveryOrderStatus.ACCEPTED.getCode(),
                    DeliveryOrderStatus.PICKING_UP.getCode(),
                    DeliveryOrderStatus.DELIVERING.getCode());
            long deliveringCount = this.count(wrapper);
            if (deliveringCount <= 0) {
                riderService.updateWorkStatus(oldRiderId, RiderWorkStatus.IDLE.getCode());
            }
        }

        dispatchRecordService.addDispatchRecord(
                id, deliveryOrder.getDeliveryNo(), riderId, rider.getName(),
                3, "后台手动派单", deliveryOrder.getDistance(), deliveryOrder.getEstimateTime()
        );

        return true;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean transferOrder(Long id, Long fromRiderId, Long toRiderId, String reason) {
        DeliveryOrder deliveryOrder = this.getById(id);
        if (deliveryOrder == null) {
            throw new BusinessException(ResultCode.DATA_NOT_EXIST);
        }
        if (!DeliveryOrderStatus.ACCEPTED.getCode().equals(deliveryOrder.getStatus())
                && !DeliveryOrderStatus.PICKING_UP.getCode().equals(deliveryOrder.getStatus())
                && !DeliveryOrderStatus.DELIVERING.getCode().equals(deliveryOrder.getStatus())) {
            throw new BusinessException("配送单状态不支持转单");
        }
        if (deliveryOrder.getRiderId() == null || !deliveryOrder.getRiderId().equals(fromRiderId)) {
            throw new BusinessException("配送单不属于该骑手");
        }

        Rider toRider = riderService.getById(toRiderId);
        if (toRider == null) {
            throw new BusinessException("目标骑手不存在");
        }
        if (!RiderStatus.ON_JOB.getCode().equals(toRider.getStatus())) {
            throw new BusinessException("目标骑手当前状态不可接单");
        }

        deliveryOrder.setRiderId(toRiderId);
        this.updateById(deliveryOrder);

        if (RiderWorkStatus.IDLE.getCode().equals(toRider.getWorkStatus())) {
            riderService.updateWorkStatus(toRiderId, RiderWorkStatus.DELIVERING.getCode());
        }

        LambdaQueryWrapper<DeliveryOrder> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(DeliveryOrder::getRiderId, fromRiderId);
        wrapper.in(DeliveryOrder::getStatus,
                DeliveryOrderStatus.ACCEPTED.getCode(),
                DeliveryOrderStatus.PICKING_UP.getCode(),
                DeliveryOrderStatus.DELIVERING.getCode());
        long deliveringCount = this.count(wrapper);
        if (deliveringCount <= 0) {
            riderService.updateWorkStatus(fromRiderId, RiderWorkStatus.IDLE.getCode());
        }

        dispatchRecordService.addDispatchRecord(
                id, deliveryOrder.getDeliveryNo(), toRiderId, toRider.getName(),
                4, reason != null ? reason : "转单", deliveryOrder.getDistance(), deliveryOrder.getEstimateTime()
        );

        return true;
    }

    @Override
    public Page<DeliveryOrderVO> riderOrderList(Long riderId, Integer status, Integer pageNum, Integer pageSize) {
        LambdaQueryWrapper<DeliveryOrder> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(DeliveryOrder::getRiderId, riderId);
        if (status != null) {
            wrapper.eq(DeliveryOrder::getStatus, status);
        }
        wrapper.orderByDesc(DeliveryOrder::getCreateTime);

        Page<DeliveryOrder> page = this.page(new Page<>(pageNum, pageSize), wrapper);
        Page<DeliveryOrderVO> result = new Page<>(page.getCurrent(), page.getSize(), page.getTotal());
        result.setRecords(page.getRecords().stream().map(this::convertToVO).toList());
        return result;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean redispatchOrder(Long id, String reason) {
        DeliveryOrder deliveryOrder = this.getById(id);
        if (deliveryOrder == null) {
            throw new BusinessException(ResultCode.DATA_NOT_EXIST);
        }
        if (!DeliveryOrderStatus.ACCEPTED.getCode().equals(deliveryOrder.getStatus())
                && !DeliveryOrderStatus.PICKING_UP.getCode().equals(deliveryOrder.getStatus())
                && !DeliveryOrderStatus.DELIVERING.getCode().equals(deliveryOrder.getStatus())) {
            throw new BusinessException("配送单状态不支持二次调度");
        }

        Long oldRiderId = deliveryOrder.getRiderId();

        DispatchStrategy strategy = dispatchStrategyFactory.getStrategy();
        Rider newRider = strategy.dispatch(deliveryOrder);
        if (newRider == null) {
            log.warn("二次调度未找到合适骑手，配送单号：{}", deliveryOrder.getDeliveryNo());
            return false;
        }

        deliveryOrder.setRiderId(newRider.getId());
        this.updateById(deliveryOrder);

        if (RiderWorkStatus.IDLE.getCode().equals(newRider.getWorkStatus())) {
            riderService.updateWorkStatus(newRider.getId(), RiderWorkStatus.DELIVERING.getCode());
        }

        if (oldRiderId != null) {
            LambdaQueryWrapper<DeliveryOrder> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(DeliveryOrder::getRiderId, oldRiderId);
            wrapper.in(DeliveryOrder::getStatus,
                    DeliveryOrderStatus.ACCEPTED.getCode(),
                    DeliveryOrderStatus.PICKING_UP.getCode(),
                    DeliveryOrderStatus.DELIVERING.getCode());
            long deliveringCount = this.count(wrapper);
            if (deliveringCount <= 0) {
                riderService.updateWorkStatus(oldRiderId, RiderWorkStatus.IDLE.getCode());
            }
        }

        dispatchRecordService.addDispatchRecord(
                id, deliveryOrder.getDeliveryNo(), newRider.getId(), newRider.getName(),
                5, reason != null ? reason : "二次调度",
                deliveryOrder.getDistance(), deliveryOrder.getEstimateTime()
        );

        log.info("二次调度成功，配送单号：{}，原骑手：{}，新骑手：{}",
                deliveryOrder.getDeliveryNo(), oldRiderId, newRider.getName());

        return true;
    }

    private void autoDispatch(DeliveryOrder deliveryOrder) {
        DispatchStrategy strategy = dispatchStrategyFactory.getStrategy();
        Rider rider = strategy.dispatch(deliveryOrder);
        if (rider != null) {
            deliveryOrder.setRiderId(rider.getId());
            deliveryOrder.setStatus(DeliveryOrderStatus.ACCEPTED.getCode());
            this.updateById(deliveryOrder);

            riderService.updateWorkStatus(rider.getId(), RiderWorkStatus.DELIVERING.getCode());

            dispatchRecordService.addDispatchRecord(
                    deliveryOrder.getId(), deliveryOrder.getDeliveryNo(),
                    rider.getId(), rider.getName(),
                    1, "系统自动派单",
                    deliveryOrder.getDistance(), deliveryOrder.getEstimateTime()
            );

            log.info("自动派单成功，配送单号：{}，骑手：{}", deliveryOrder.getDeliveryNo(), rider.getName());
        }
    }

    private LambdaQueryWrapper<DeliveryOrder> buildQueryWrapper(DeliveryOrderQueryDTO dto) {
        LambdaQueryWrapper<DeliveryOrder> wrapper = new LambdaQueryWrapper<>();
        if (StrUtil.isNotBlank(dto.getDeliveryNo())) {
            wrapper.like(DeliveryOrder::getDeliveryNo, dto.getDeliveryNo());
        }
        if (StrUtil.isNotBlank(dto.getOrderNo())) {
            wrapper.like(DeliveryOrder::getOrderNo, dto.getOrderNo());
        }
        if (dto.getRiderId() != null) {
            wrapper.eq(DeliveryOrder::getRiderId, dto.getRiderId());
        }
        if (dto.getWarehouseId() != null) {
            wrapper.eq(DeliveryOrder::getWarehouseId, dto.getWarehouseId());
        }
        if (dto.getStatus() != null) {
            wrapper.eq(DeliveryOrder::getStatus, dto.getStatus());
        }
        if (StrUtil.isNotBlank(dto.getReceiverPhone())) {
            wrapper.like(DeliveryOrder::getReceiverPhone, dto.getReceiverPhone());
        }
        wrapper.orderByDesc(DeliveryOrder::getCreateTime);
        return wrapper;
    }

    private DeliveryOrderVO convertToVO(DeliveryOrder deliveryOrder) {
        DeliveryOrderVO vo = new DeliveryOrderVO();
        BeanUtils.copyProperties(deliveryOrder, vo);
        vo.setStatusDesc(DeliveryOrderStatus.getDesc(deliveryOrder.getStatus()));
        if (deliveryOrder.getRiderId() != null) {
            Rider rider = riderService.getById(deliveryOrder.getRiderId());
            if (rider != null) {
                vo.setRiderName(rider.getName());
                vo.setRiderPhone(rider.getPhone());
            }
        }
        return vo;
    }

    private DeliveryDetailVO buildDetailVO(DeliveryOrder deliveryOrder) {
        DeliveryDetailVO vo = new DeliveryDetailVO();
        BeanUtils.copyProperties(deliveryOrder, vo);
        vo.setStatusDesc(DeliveryOrderStatus.getDesc(deliveryOrder.getStatus()));

        if (deliveryOrder.getRiderId() != null) {
            Rider rider = riderService.getById(deliveryOrder.getRiderId());
            if (rider != null) {
                vo.setRiderId(rider.getId());
                vo.setRiderName(rider.getName());
                vo.setRiderPhone(rider.getPhone());
                vo.setRiderAvatar(rider.getAvatar());
                vo.setRiderLongitude(rider.getCurrentLongitude());
                vo.setRiderLatitude(rider.getCurrentLatitude());
            }
        }

        List<DeliveryTrackVO> tracks = deliveryTrackService.getTrackList(deliveryOrder.getId());
        vo.setTracks(tracks);

        return vo;
    }

    private String generateDeliveryNo() {
        return DELIVERY_NO_PREFIX + IdUtil.getSnowflakeNextIdStr();
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

    private Integer calculateEstimateTime(double distance) {
        double speed = 20000.0 / 3600;
        return (int) Math.ceil(distance / speed / 60) + 10;
    }

    private BigDecimal calculateDeliveryFee(double distance) {
        double baseFee = 5.0;
        double perKmFee = 2.0;
        double distanceKm = distance / 1000;
        if (distanceKm <= 3) {
            return BigDecimal.valueOf(baseFee);
        }
        return BigDecimal.valueOf(baseFee + (distanceKm - 3) * perKmFee);
    }
}
