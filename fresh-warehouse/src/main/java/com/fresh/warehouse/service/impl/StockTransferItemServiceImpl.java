package com.fresh.warehouse.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.fresh.warehouse.entity.StockTransferItem;
import com.fresh.warehouse.mapper.StockTransferItemMapper;
import com.fresh.warehouse.service.StockTransferItemService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class StockTransferItemServiceImpl extends ServiceImpl<StockTransferItemMapper, StockTransferItem> implements StockTransferItemService {

    @Override
    public List<StockTransferItem> listByTransferId(Long transferId) {
        LambdaQueryWrapper<StockTransferItem> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(StockTransferItem::getTransferId, transferId);
        return list(wrapper);
    }

    @Override
    public List<StockTransferItem> listByTransferNo(String transferNo) {
        LambdaQueryWrapper<StockTransferItem> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(StockTransferItem::getTransferNo, transferNo);
        return list(wrapper);
    }
}
