package com.fresh.order.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.fresh.order.entity.OrderItem;

import java.util.List;

public interface OrderItemService extends IService<OrderItem> {

    List<OrderItem> listByOrderId(Long orderId);

    List<OrderItem> listByOrderIds(List<Long> orderIds);

}
