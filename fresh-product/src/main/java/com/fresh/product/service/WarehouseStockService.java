package com.fresh.product.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.fresh.product.dto.StockLockDTO;
import com.fresh.product.dto.StockTransferDTO;
import com.fresh.product.entity.WarehouseStock;
import com.fresh.product.vo.StockVO;

import java.util.List;

public interface WarehouseStockService extends IService<WarehouseStock> {

    List<StockVO> getStockBySkuId(Long skuId);

    StockVO getStockByWarehouseAndSku(Long warehouseId, Long skuId);

    boolean lockStock(StockLockDTO dto);

    boolean releaseStock(StockLockDTO dto);

    boolean deductStock(StockLockDTO dto);

    boolean restoreStock(StockLockDTO dto);

    boolean stockIn(Long warehouseId, Long skuId, Integer qty, String orderNo, String remark);

    boolean stockOut(Long warehouseId, Long skuId, Integer qty, String orderNo, String remark);

    boolean transferStock(StockTransferDTO dto);

    void initStock(Long warehouseId, Long skuId, Integer qty);
}
