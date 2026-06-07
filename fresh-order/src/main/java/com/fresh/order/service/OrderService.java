package com.fresh.order.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.fresh.order.dto.OrderCreateDTO;
import com.fresh.order.dto.OrderQueryDTO;
import com.fresh.order.entity.Order;
import com.fresh.order.vo.OrderDetailVO;
import com.fresh.order.vo.OrderVO;

public interface OrderService extends IService<Order> {

    String createOrder(OrderCreateDTO dto);

    Page<OrderVO> orderList(OrderQueryDTO dto);

    OrderDetailVO orderDetail(Long id);

    Boolean cancelOrder(Long id);

    Boolean confirmReceive(Long id);

    Boolean payCallback(String orderNo);

    Boolean deliverOrder(Long id, Long warehouseId, Long riderId);

    Boolean transferWarehouse(Long id, Long fromWarehouseId, Long toWarehouseId);

    Page<OrderVO> adminOrderList(OrderQueryDTO dto);

    void cancelTimeoutOrder(String orderNo);

}
