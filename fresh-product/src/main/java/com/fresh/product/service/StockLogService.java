package com.fresh.product.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.fresh.product.entity.StockLog;

import java.util.List;

public interface StockLogService extends IService<StockLog> {

    List<StockLog> list(Long warehouseId, Long skuId, Integer type);

    void addStockLog(Long warehouseId, Long skuId, Integer type, Integer qty,
                     Integer beforeQty, Integer afterQty, String orderNo, String remark);
}
