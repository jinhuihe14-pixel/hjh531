package com.fresh.order.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.fresh.order.entity.OrderWarehouse;

import java.util.List;

public interface OrderWarehouseService extends IService<OrderWarehouse> {

    List<OrderWarehouse> listByOrderId(Long orderId);

    List<OrderWarehouse> listByOrderIds(List<Long> orderIds);

    Boolean updateByWarehouse(Long orderId, Long warehouseId, Long riderId);

    Boolean transferWarehouse(Long orderId, Long fromWarehouseId, Long toWarehouseId);

}
