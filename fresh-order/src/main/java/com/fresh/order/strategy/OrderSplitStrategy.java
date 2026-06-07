package com.fresh.order.strategy;

import com.fresh.order.dto.OrderItemDTO;

import java.math.BigDecimal;
import java.util.List;

public interface OrderSplitStrategy {

    SplitResult split(List<OrderItemDTO> items, BigDecimal longitude, BigDecimal latitude);

    String getStrategyName();

}
