package com.fresh.order.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.fresh.order.entity.OrderWarehouse;
import com.fresh.order.enums.OrderStatus;
import com.fresh.order.mapper.OrderWarehouseMapper;
import com.fresh.order.service.OrderWarehouseService;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class OrderWarehouseServiceImpl extends ServiceImpl<OrderWarehouseMapper, OrderWarehouse>
        implements OrderWarehouseService {

    @Resource
    private OrderWarehouseMapper orderWarehouseMapper;

    @Override
    public List<OrderWarehouse> listByOrderId(Long orderId) {
        return orderWarehouseMapper.selectByOrderId(orderId);
    }

    @Override
    public List<OrderWarehouse> listByOrderIds(List<Long> orderIds) {
        return orderWarehouseMapper.selectByOrderIds(orderIds);
    }

    @Override
    public Boolean updateByWarehouse(Long orderId, Long warehouseId, Long riderId) {
        LambdaQueryWrapper<OrderWarehouse> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(OrderWarehouse::getOrderId, orderId)
                .eq(OrderWarehouse::getWarehouseId, warehouseId);
        OrderWarehouse orderWarehouse = getOne(wrapper);
        if (orderWarehouse == null) {
            return false;
        }
        orderWarehouse.setStatus(OrderStatus.DELIVERING.getCode());
        orderWarehouse.setRiderId(riderId);
        orderWarehouse.setDeliveryTime(LocalDateTime.now());
        return updateById(orderWarehouse);
    }

    @Override
    public Boolean transferWarehouse(Long orderId, Long fromWarehouseId, Long toWarehouseId) {
        LambdaQueryWrapper<OrderWarehouse> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(OrderWarehouse::getOrderId, orderId)
                .eq(OrderWarehouse::getWarehouseId, fromWarehouseId);
        OrderWarehouse orderWarehouse = getOne(wrapper);
        if (orderWarehouse == null) {
            return false;
        }
        orderWarehouse.setWarehouseId(toWarehouseId);
        return updateById(orderWarehouse);
    }

}
