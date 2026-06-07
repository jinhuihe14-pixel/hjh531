package com.fresh.aftersale.strategy;

import com.fresh.aftersale.entity.AftersaleOrder;
import com.fresh.aftersale.feign.ProductFeignClient;
import com.fresh.aftersale.service.AftersaleItemService;
import jakarta.annotation.Resource;

public abstract class AbstractAftersaleStrategy implements AftersaleStrategy {

    @Resource
    protected AftersaleItemService aftersaleItemService;

    @Resource
    protected ProductFeignClient productFeignClient;

    @Override
    public void processAftersale(AftersaleOrder order) {
    }

    @Override
    public void processRefund(AftersaleOrder order) {
    }

    @Override
    public void restoreStock(AftersaleOrder order) {
    }
}
