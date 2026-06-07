package com.fresh.warehouse.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.fresh.warehouse.entity.StockTransferItem;

import java.util.List;

public interface StockTransferItemService extends IService<StockTransferItem> {

    List<StockTransferItem> listByTransferId(Long transferId);

    List<StockTransferItem> listByTransferNo(String transferNo);
}
