package com.fresh.order.service.impl;

import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.fresh.common.context.UserContext;
import com.fresh.common.exception.BusinessException;
import com.fresh.common.lock.DistributedLock;
import com.fresh.common.result.ResultCode;
import com.fresh.common.util.RedisUtil;
import com.fresh.order.dto.OrderCreateDTO;
import com.fresh.order.dto.OrderItemDTO;
import com.fresh.order.dto.OrderQueryDTO;
import com.fresh.order.entity.*;
import com.fresh.order.enums.OrderStatus;
import com.fresh.order.feign.ProductFeignClient;
import com.fresh.order.mapper.OrderMapper;
import com.fresh.order.service.*;
import com.fresh.order.strategy.OrderSplitStrategyFactory;
import com.fresh.order.strategy.SplitResult;
import com.fresh.order.vo.*;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.stream.function.StreamBridge;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Slf4j
@Service
public class OrderServiceImpl extends ServiceImpl<OrderMapper, Order> implements OrderService {

    @Resource
    private OrderItemService orderItemService;

    @Resource
    private OrderWarehouseService orderWarehouseService;

    @Resource
    private OrderLogService orderLogService;

    @Resource
    private ProductFeignClient productFeignClient;

    @Resource
    private OrderSplitStrategyFactory orderSplitStrategyFactory;

    @Resource
    private RedisUtil redisUtil;

    @Resource
    private StreamBridge streamBridge;

    @Value("${order.timeout.pay-minutes:30}")
    private Integer payTimeoutMinutes;

    private static final String ORDER_NO_PREFIX = "FD";
    private static final String ORDER_LOCK_PREFIX = "order:lock:";
    private static final String ORDER_STOCK_LOCK_PREFIX = "order:stock:lock:";

    @Override
    @Transactional(rollbackFor = Exception.class)
    @DistributedLock(key = "#dto.hashCode()", prefix = "order:create:")
    public String createOrder(OrderCreateDTO dto) {
        Long userId = UserContext.getUserId();
        if (userId == null) {
            throw new BusinessException(ResultCode.UNAUTHORIZED);
        }

        String orderNo = generateOrderNo();

        BigDecimal totalAmount = BigDecimal.ZERO;
        BigDecimal payAmount = BigDecimal.ZERO;

        SplitResult splitResult = orderSplitStrategyFactory.getStrategy().split(
                dto.getItems(), dto.getLongitude(), dto.getLatitude());

        if (splitResult.getWarehouseAllocations().isEmpty()) {
            throw new BusinessException("拆单失败，没有可用仓库");
        }

        preDeductStock(dto.getItems(), orderNo);

        Order order = buildOrder(dto, userId, orderNo, totalAmount, payAmount);
        save(order);

        saveOrderItems(order, dto.getItems(), splitResult);

        saveOrderWarehouses(order, splitResult);

        saveOrderLog(order.getId(), orderNo, null, OrderStatus.PENDING_PAYMENT.getCode(),
                "CREATE", "创建订单", userId, "用户");

        sendDelayMessage(orderNo);

        return orderNo;
    }

    @Override
    public Page<OrderVO> orderList(OrderQueryDTO dto) {
        Long userId = UserContext.getUserId();
        if (userId == null) {
            throw new BusinessException(ResultCode.UNAUTHORIZED);
        }

        Page<Order> page = new Page<>(dto.getPageNum(), dto.getPageSize());
        LambdaQueryWrapper<Order> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Order::getUserId, userId);

        if (dto.getStatus() != null) {
            wrapper.eq(Order::getStatus, dto.getStatus());
        }
        if (StrUtil.isNotBlank(dto.getOrderNo())) {
            wrapper.like(Order::getOrderNo, dto.getOrderNo());
        }

        wrapper.orderByDesc(Order::getCreateTime);
        Page<Order> orderPage = page(page, wrapper);

        Page<OrderVO> result = new Page<>(orderPage.getCurrent(), orderPage.getSize(), orderPage.getTotal());
        if (orderPage.getRecords().isEmpty()) {
            return result;
        }

        List<Long> orderIds = orderPage.getRecords().stream().map(Order::getId).collect(Collectors.toList());
        List<OrderItem> orderItems = orderItemService.listByOrderIds(orderIds);
        Map<Long, List<OrderItem>> itemMap = orderItems.stream()
                .collect(Collectors.groupingBy(OrderItem::getOrderId));

        List<OrderVO> voList = orderPage.getRecords().stream().map(order -> {
            OrderVO vo = new OrderVO();
            BeanUtils.copyProperties(order, vo);
            OrderStatus status = OrderStatus.getByCode(order.getStatus());
            if (status != null) {
                vo.setStatusDesc(status.getDesc());
            }
            List<OrderItem> items = itemMap.getOrDefault(order.getId(), Collections.emptyList());
            vo.setItems(convertToItemVO(items));
            return vo;
        }).collect(Collectors.toList());

        result.setRecords(voList);
        return result;
    }

    @Override
    public OrderDetailVO orderDetail(Long id) {
        Order order = getById(id);
        if (order == null) {
            throw new BusinessException(ResultCode.ORDER_NOT_EXIST);
        }

        Long userId = UserContext.getUserId();
        if (userId != null && !userId.equals(order.getUserId())) {
            throw new BusinessException(ResultCode.FORBIDDEN);
        }

        OrderDetailVO vo = new OrderDetailVO();
        BeanUtils.copyProperties(order, vo);
        OrderStatus status = OrderStatus.getByCode(order.getStatus());
        if (status != null) {
            vo.setStatusDesc(status.getDesc());
        }

        List<OrderItem> orderItems = orderItemService.listByOrderId(id);
        vo.setItems(convertToItemVO(orderItems));

        List<OrderWarehouse> warehouses = orderWarehouseService.listByOrderId(id);
        vo.setWarehouses(convertToWarehouseVO(warehouses));

        return vo;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean cancelOrder(Long id) {
        Order order = getById(id);
        if (order == null) {
            throw new BusinessException(ResultCode.ORDER_NOT_EXIST);
        }

        Long userId = UserContext.getUserId();
        if (userId != null && !userId.equals(order.getUserId())) {
            throw new BusinessException(ResultCode.FORBIDDEN);
        }

        if (!OrderStatus.PENDING_PAYMENT.getCode().equals(order.getStatus())) {
            throw new BusinessException("订单状态不允许取消");
        }

        order.setStatus(OrderStatus.CANCELLED.getCode());
        updateById(order);

        releaseStock(order.getOrderNo());

        saveOrderLog(order.getId(), order.getOrderNo(), OrderStatus.PENDING_PAYMENT.getCode(),
                OrderStatus.CANCELLED.getCode(), "CANCEL", "用户取消订单", userId, "用户");

        return true;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean confirmReceive(Long id) {
        Order order = getById(id);
        if (order == null) {
            throw new BusinessException(ResultCode.ORDER_NOT_EXIST);
        }

        Long userId = UserContext.getUserId();
        if (userId != null && !userId.equals(order.getUserId())) {
            throw new BusinessException(ResultCode.FORBIDDEN);
        }

        if (!OrderStatus.DELIVERING.getCode().equals(order.getStatus())) {
            throw new BusinessException("订单状态不允许确认收货");
        }

        Integer beforeStatus = order.getStatus();
        order.setStatus(OrderStatus.COMPLETED.getCode());
        order.setCompleteTime(LocalDateTime.now());
        updateById(order);

        saveOrderLog(order.getId(), order.getOrderNo(), beforeStatus,
                OrderStatus.COMPLETED.getCode(), "COMPLETE", "用户确认收货", userId, "用户");

        return true;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean payCallback(String orderNo) {
        LambdaQueryWrapper<Order> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Order::getOrderNo, orderNo);
        Order order = getOne(wrapper);
        if (order == null) {
            throw new BusinessException(ResultCode.ORDER_NOT_EXIST);
        }

        if (!OrderStatus.PENDING_PAYMENT.getCode().equals(order.getStatus())) {
            log.warn("订单状态不是待支付，orderNo: {}", orderNo);
            return false;
        }

        Integer beforeStatus = order.getStatus();
        order.setStatus(OrderStatus.PENDING_SHIPMENT.getCode());
        order.setPayTime(LocalDateTime.now());
        updateById(order);

        confirmDeductStock(orderNo);

        sendDeliveryNotification(order);

        saveOrderLog(order.getId(), order.getOrderNo(), beforeStatus,
                OrderStatus.PENDING_SHIPMENT.getCode(), "PAY", "支付成功", null, "系统");

        return true;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean deliverOrder(Long id, Long warehouseId, Long riderId) {
        Order order = getById(id);
        if (order == null) {
            throw new BusinessException(ResultCode.ORDER_NOT_EXIST);
        }

        if (!OrderStatus.PENDING_SHIPMENT.getCode().equals(order.getStatus())) {
            throw new BusinessException("订单状态不允许发货");
        }

        Integer beforeStatus = order.getStatus();
        order.setStatus(OrderStatus.DELIVERING.getCode());
        order.setDeliveryTime(LocalDateTime.now());
        updateById(order);

        orderWarehouseService.updateByWarehouse(id, warehouseId, riderId);

        saveOrderLog(order.getId(), order.getOrderNo(), beforeStatus,
                OrderStatus.DELIVERING.getCode(), "DELIVER", "订单发货", null, "系统");

        return true;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean transferWarehouse(Long id, Long fromWarehouseId, Long toWarehouseId) {
        Order order = getById(id);
        if (order == null) {
            throw new BusinessException(ResultCode.ORDER_NOT_EXIST);
        }

        if (!OrderStatus.PENDING_SHIPMENT.getCode().equals(order.getStatus())) {
            throw new BusinessException("订单状态不允许改派仓库");
        }

        orderWarehouseService.transferWarehouse(id, fromWarehouseId, toWarehouseId);

        saveOrderLog(order.getId(), order.getOrderNo(), order.getStatus(),
                order.getStatus(), "TRANSFER",
                "改派仓库: 从" + fromWarehouseId + "到" + toWarehouseId, null, "系统");

        return true;
    }

    @Override
    public Page<OrderVO> adminOrderList(OrderQueryDTO dto) {
        Page<Order> page = new Page<>(dto.getPageNum(), dto.getPageSize());
        LambdaQueryWrapper<Order> wrapper = new LambdaQueryWrapper<>();

        if (dto.getStatus() != null) {
            wrapper.eq(Order::getStatus, dto.getStatus());
        }
        if (StrUtil.isNotBlank(dto.getOrderNo())) {
            wrapper.like(Order::getOrderNo, dto.getOrderNo());
        }
        if (dto.getUserId() != null) {
            wrapper.eq(Order::getUserId, dto.getUserId());
        }

        wrapper.orderByDesc(Order::getCreateTime);
        Page<Order> orderPage = page(page, wrapper);

        Page<OrderVO> result = new Page<>(orderPage.getCurrent(), orderPage.getSize(), orderPage.getTotal());
        if (orderPage.getRecords().isEmpty()) {
            return result;
        }

        List<Long> orderIds = orderPage.getRecords().stream().map(Order::getId).collect(Collectors.toList());
        List<OrderItem> orderItems = orderItemService.listByOrderIds(orderIds);
        Map<Long, List<OrderItem>> itemMap = orderItems.stream()
                .collect(Collectors.groupingBy(OrderItem::getOrderId));

        List<OrderVO> voList = orderPage.getRecords().stream().map(order -> {
            OrderVO vo = new OrderVO();
            BeanUtils.copyProperties(order, vo);
            OrderStatus status = OrderStatus.getByCode(order.getStatus());
            if (status != null) {
                vo.setStatusDesc(status.getDesc());
            }
            List<OrderItem> items = itemMap.getOrDefault(order.getId(), Collections.emptyList());
            vo.setItems(convertToItemVO(items));
            return vo;
        }).collect(Collectors.toList());

        result.setRecords(voList);
        return result;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void cancelTimeoutOrder(String orderNo) {
        LambdaQueryWrapper<Order> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Order::getOrderNo, orderNo);
        Order order = getOne(wrapper);
        if (order == null) {
            log.warn("超时取消订单不存在，orderNo: {}", orderNo);
            return;
        }

        if (!OrderStatus.PENDING_PAYMENT.getCode().equals(order.getStatus())) {
            log.info("订单已支付或已取消，无需超时取消，orderNo: {}", orderNo);
            return;
        }

        Integer beforeStatus = order.getStatus();
        order.setStatus(OrderStatus.CANCELLED.getCode());
        updateById(order);

        releaseStock(orderNo);

        saveOrderLog(order.getId(), order.getOrderNo(), beforeStatus,
                OrderStatus.CANCELLED.getCode(), "TIMEOUT_CANCEL", "超时未支付自动取消", null, "系统");

        log.info("超时订单已取消，orderNo: {}", orderNo);
    }

    private String generateOrderNo() {
        String snowflakeId = IdUtil.getSnowflakeNextIdStr();
        return ORDER_NO_PREFIX + snowflakeId;
    }

    private Order buildOrder(OrderCreateDTO dto, Long userId, String orderNo,
                              BigDecimal totalAmount, BigDecimal payAmount) {
        Order order = new Order();
        order.setOrderNo(orderNo);
        order.setUserId(userId);
        order.setTotalAmount(totalAmount);
        order.setPayAmount(payAmount);
        order.setStatus(OrderStatus.PENDING_PAYMENT.getCode());
        order.setAddressId(dto.getAddressId());
        order.setReceiverName(dto.getReceiverName());
        order.setReceiverPhone(dto.getReceiverPhone());
        order.setReceiverAddress(dto.getReceiverAddress());
        order.setLongitude(dto.getLongitude());
        order.setLatitude(dto.getLatitude());
        order.setRemark(dto.getRemark());
        return order;
    }

    private void saveOrderItems(Order order, List<OrderItemDTO> items, SplitResult splitResult) {
        Map<Long, Long> skuWarehouseMap = new HashMap<>();
        for (SplitResult.WarehouseAllocation wa : splitResult.getWarehouseAllocations()) {
            for (SplitResult.SkuAllocation sa : wa.getSkuAllocations()) {
                skuWarehouseMap.put(sa.getSkuId(), wa.getWarehouseId());
            }
        }

        Map<Long, OrderItemDTO> itemDtoMap = items.stream()
                .collect(Collectors.toMap(OrderItemDTO::getSkuId, o -> o));

        for (SplitResult.WarehouseAllocation wa : splitResult.getWarehouseAllocations()) {
            for (SplitResult.SkuAllocation sa : wa.getSkuAllocations()) {
                OrderItemDTO itemDto = itemDtoMap.get(sa.getSkuId());
                OrderItem orderItem = new OrderItem();
                orderItem.setOrderId(order.getId());
                orderItem.setOrderNo(order.getOrderNo());
                orderItem.setSkuId(sa.getSkuId());
                orderItem.setSkuName(itemDto.getSkuName());
                orderItem.setSkuImage(itemDto.getSkuImage());
                orderItem.setPrice(itemDto.getPrice());
                orderItem.setQuantity(sa.getQuantity());
                orderItem.setSubtotal(itemDto.getPrice().multiply(new BigDecimal(sa.getQuantity())));
                orderItem.setWarehouseId(wa.getWarehouseId());
                orderItemService.save(orderItem);
            }
        }
    }

    private void saveOrderWarehouses(Order order, SplitResult splitResult) {
        for (SplitResult.WarehouseAllocation wa : splitResult.getWarehouseAllocations()) {
            OrderWarehouse orderWarehouse = new OrderWarehouse();
            orderWarehouse.setOrderId(order.getId());
            orderWarehouse.setOrderNo(order.getOrderNo());
            orderWarehouse.setWarehouseId(wa.getWarehouseId());
            orderWarehouse.setWarehouseName(wa.getWarehouseName());
            orderWarehouse.setStatus(OrderStatus.PENDING_SHIPMENT.getCode());
            orderWarehouse.setDeliveryFee(BigDecimal.ZERO);
            orderWarehouseService.save(orderWarehouse);
        }
    }

    private void saveOrderLog(Long orderId, String orderNo, Integer beforeStatus, Integer afterStatus,
                               String operateType, String operateRemark, Long operatorId, String operatorName) {
        OrderLog orderLog = new OrderLog();
        orderLog.setOrderId(orderId);
        orderLog.setOrderNo(orderNo);
        orderLog.setBeforeStatus(beforeStatus);
        orderLog.setAfterStatus(afterStatus);
        orderLog.setOperateType(operateType);
        orderLog.setOperateRemark(operateRemark);
        orderLog.setOperatorId(operatorId);
        orderLog.setOperatorName(operatorName);
        orderLogService.save(orderLog);
    }

    private void preDeductStock(List<OrderItemDTO> items, String orderNo) {
        try {
            List<Map<String, Object>> stockItems = items.stream().map(item -> {
                Map<String, Object> map = new HashMap<>();
                map.put("skuId", item.getSkuId());
                map.put("quantity", item.getQuantity());
                return map;
            }).collect(Collectors.toList());

            var result = productFeignClient.batchPreDeductStock(stockItems, orderNo);
            if (result == null || result.getCode() != 200 || !Boolean.TRUE.equals(result.getData())) {
                throw new BusinessException("库存预扣失败");
            }
        } catch (BusinessException e) {
            throw e;
        } catch (Exception e) {
            log.error("预扣库存异常", e);
            throw new BusinessException("库存预扣失败");
        }
    }

    private void confirmDeductStock(String orderNo) {
        try {
            productFeignClient.confirmDeductStock(orderNo);
        } catch (Exception e) {
            log.error("确认扣减库存异常，orderNo: {}", orderNo, e);
        }
    }

    private void releaseStock(String orderNo) {
        try {
            productFeignClient.releaseStock(orderNo);
        } catch (Exception e) {
            log.error("释放库存异常，orderNo: {}", orderNo, e);
        }
    }

    private void sendDelayMessage(String orderNo) {
        try {
            Map<String, Object> message = new HashMap<>();
            message.put("orderNo", orderNo);
            message.put("createTime", System.currentTimeMillis());
            streamBridge.send("order-delay-output", message);
        } catch (Exception e) {
            log.error("发送延迟消息失败，orderNo: {}", orderNo, e);
            redisUtil.set(ORDER_LOCK_PREFIX + orderNo, "1",
                    payTimeoutMinutes * 60L + 5 * 60, TimeUnit.SECONDS);
        }
    }

    private void sendDeliveryNotification(Order order) {
        log.info("发送配送通知，orderNo: {}", order.getOrderNo());
    }

    private List<OrderItemVO> convertToItemVO(List<OrderItem> items) {
        return items.stream().map(item -> {
            OrderItemVO vo = new OrderItemVO();
            BeanUtils.copyProperties(item, vo);
            return vo;
        }).collect(Collectors.toList());
    }

    private List<OrderWarehouseVO> convertToWarehouseVO(List<OrderWarehouse> warehouses) {
        return warehouses.stream().map(wh -> {
            OrderWarehouseVO vo = new OrderWarehouseVO();
            BeanUtils.copyProperties(wh, vo);
            OrderStatus status = OrderStatus.getByCode(wh.getStatus());
            if (status != null) {
                vo.setStatusDesc(status.getDesc());
            }
            return vo;
        }).collect(Collectors.toList());
    }

}
