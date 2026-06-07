package com.fresh.delivery.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.fresh.delivery.dto.DeliveryOrderCreateDTO;
import com.fresh.delivery.dto.DeliveryOrderQueryDTO;
import com.fresh.delivery.entity.DeliveryOrder;
import com.fresh.delivery.vo.DeliveryDetailVO;
import com.fresh.delivery.vo.DeliveryOrderVO;

public interface DeliveryOrderService extends IService<DeliveryOrder> {

    String createDeliveryOrder(DeliveryOrderCreateDTO dto);

    Page<DeliveryOrderVO> deliveryOrderPage(DeliveryOrderQueryDTO dto);

    DeliveryDetailVO deliveryDetail(Long id);

    DeliveryDetailVO deliveryDetailByOrderNo(String orderNo);

    Boolean acceptOrder(Long id, Long riderId);

    Boolean pickupConfirm(Long id);

    Boolean deliveryConfirm(Long id);

    Boolean cancelDelivery(Long id, String reason);

    Boolean manualDispatch(Long id, Long riderId);

    Boolean transferOrder(Long id, Long fromRiderId, Long toRiderId, String reason);

    Page<DeliveryOrderVO> riderOrderList(Long riderId, Integer status, Integer pageNum, Integer pageSize);

    Boolean redispatchOrder(Long id, String reason);

}
