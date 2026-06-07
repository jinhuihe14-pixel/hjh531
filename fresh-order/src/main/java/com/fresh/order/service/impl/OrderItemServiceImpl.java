package com.fresh.order.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.fresh.order.entity.OrderItem;
import com.fresh.order.mapper.OrderItemMapper;
import com.fresh.order.service.OrderItemService;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class OrderItemServiceImpl extends ServiceImpl<OrderItemMapper, OrderItem> implements OrderItemService {

    @Resource
    private OrderItemMapper orderItemMapper;

    @Override
    public List<OrderItem> listByOrderId(Long orderId) {
        return orderItemMapper.selectByOrderId(orderId);
    }

    @Override
    public List<OrderItem> listByOrderIds(List<Long> orderIds) {
        return orderItemMapper.selectByOrderIds(orderIds);
    }

}
