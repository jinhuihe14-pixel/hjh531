package com.fresh.order.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.fresh.order.entity.OrderItem;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface OrderItemMapper extends BaseMapper<OrderItem> {

    List<OrderItem> selectByOrderId(Long orderId);

    List<OrderItem> selectByOrderIds(List<Long> orderIds);

}
