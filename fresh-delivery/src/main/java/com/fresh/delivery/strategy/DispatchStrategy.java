package com.fresh.delivery.strategy;

import com.fresh.delivery.entity.DeliveryOrder;
import com.fresh.delivery.entity.Rider;

public interface DispatchStrategy {

    Rider dispatch(DeliveryOrder deliveryOrder);

    String getStrategyName();

}
