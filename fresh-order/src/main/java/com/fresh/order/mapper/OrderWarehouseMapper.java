package com.fresh.order.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.fresh.order.entity.OrderWarehouse;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface OrderWarehouseMapper extends BaseMapper<OrderWarehouse> {

    List<OrderWarehouse> selectByOrderId(Long orderId);

    List<OrderWarehouse> selectByOrderIds(List<Long> orderIds);

}
