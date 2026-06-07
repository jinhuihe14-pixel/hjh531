package com.fresh.product.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.fresh.product.entity.StockLog;
import com.fresh.product.mapper.StockLogMapper;
import com.fresh.product.service.StockLogService;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class StockLogServiceImpl extends ServiceImpl<StockLogMapper, StockLog> implements StockLogService {

    @Override
    public List<StockLog> list(Long warehouseId, Long skuId, Integer type) {
        LambdaQueryWrapper<StockLog> wrapper = new LambdaQueryWrapper<>();
        if (warehouseId != null) {
            wrapper.eq(StockLog::getWarehouseId, warehouseId);
        }
        if (skuId != null) {
            wrapper.eq(StockLog::getSkuId, skuId);
        }
        if (type != null) {
            wrapper.eq(StockLog::getType, type);
        }
        wrapper.orderByDesc(StockLog::getCreateTime);
        return list(wrapper);
    }

    @Override
    public void addStockLog(Long warehouseId, Long skuId, Integer type, Integer qty,
                            Integer beforeQty, Integer afterQty, String orderNo, String remark) {
        StockLog log = new StockLog();
        log.setWarehouseId(warehouseId);
        log.setSkuId(skuId);
        log.setType(type);
        log.setQty(qty);
        log.setBeforeQty(beforeQty);
        log.setAfterQty(afterQty);
        log.setOrderNo(orderNo);
        log.setRemark(remark);
        save(log);
    }
}
