package com.fresh.product.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson2.JSON;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.fresh.common.exception.BusinessException;
import com.fresh.common.lock.DistributedLock;
import com.fresh.common.result.ResultCode;
import com.fresh.common.util.RedisUtil;
import com.fresh.product.constants.StockConstants;
import com.fresh.product.dto.StockLockDTO;
import com.fresh.product.dto.StockTransferDTO;
import com.fresh.product.entity.ProductSku;
import com.fresh.product.entity.Warehouse;
import com.fresh.product.entity.WarehouseStock;
import com.fresh.product.mapper.WarehouseStockMapper;
import com.fresh.product.service.ProductSkuService;
import com.fresh.product.service.StockLogService;
import com.fresh.product.service.WarehouseService;
import com.fresh.product.service.WarehouseStockService;
import com.fresh.product.vo.StockVO;
import jakarta.annotation.Resource;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Service
public class WarehouseStockServiceImpl extends ServiceImpl<WarehouseStockMapper, WarehouseStock> implements WarehouseStockService {

    @Resource
    private RedisUtil redisUtil;

    @Resource
    private StockLogService stockLogService;

    @Resource
    private WarehouseService warehouseService;

    @Resource
    private ProductSkuService productSkuService;

    @Value("${product.stock.cache-prefix:product:stock:}")
    private String stockCachePrefix;

    @Value("${product.stock.cache-expire:3600}")
    private Long cacheExpire;

    @Override
    public List<StockVO> getStockBySkuId(Long skuId) {
        List<WarehouseStock> stockList = list(new LambdaQueryWrapper<WarehouseStock>()
                .eq(WarehouseStock::getSkuId, skuId));

        if (stockList.isEmpty()) {
            return new ArrayList<>();
        }

        List<Long> warehouseIds = stockList.stream()
                .map(WarehouseStock::getWarehouseId)
                .collect(Collectors.toList());
        List<Warehouse> warehouses = warehouseService.listByIds(warehouseIds);
        ProductSku sku = productSkuService.getById(skuId);

        return stockList.stream().map(stock -> {
            StockVO vo = BeanUtil.copyProperties(stock, StockVO.class);
            warehouses.stream()
                    .filter(w -> w.getId().equals(stock.getWarehouseId()))
                    .findFirst()
                    .ifPresent(w -> vo.setWarehouseName(w.getName()));
            if (sku != null) {
                vo.setSkuName(sku.getSkuName());
            }
            return vo;
        }).collect(Collectors.toList());
    }

    @Override
    public StockVO getStockByWarehouseAndSku(Long warehouseId, Long skuId) {
        String cacheKey = stockCachePrefix + warehouseId + ":" + skuId;
        String cacheValue = redisUtil.get(cacheKey);

        if (StrUtil.isNotBlank(cacheValue)) {
            return JSON.parseObject(cacheValue, StockVO.class);
        }

        WarehouseStock stock = getOne(new LambdaQueryWrapper<WarehouseStock>()
                .eq(WarehouseStock::getWarehouseId, warehouseId)
                .eq(WarehouseStock::getSkuId, skuId));

        if (stock == null) {
            return null;
        }

        StockVO vo = BeanUtil.copyProperties(stock, StockVO.class);
        Warehouse warehouse = warehouseService.getById(warehouseId);
        if (warehouse != null) {
            vo.setWarehouseName(warehouse.getName());
        }
        ProductSku sku = productSkuService.getById(skuId);
        if (sku != null) {
            vo.setSkuName(sku.getSkuName());
        }

        redisUtil.set(cacheKey, JSON.toJSONString(vo), cacheExpire, TimeUnit.SECONDS);

        return vo;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    @DistributedLock(key = "#dto.warehouseId + '_' + #dto.skuId", prefix = "stock:lock:", expire = 10, timeUnit = java.util.concurrent.TimeUnit.SECONDS)
    public boolean lockStock(StockLockDTO dto) {
        WarehouseStock stock = getStock(dto.getWarehouseId(), dto.getSkuId());
        if (stock == null) {
            throw new BusinessException("库存不存在");
        }

        int beforeAvailable = stock.getAvailableQty();
        if (beforeAvailable < dto.getQty()) {
            throw new BusinessException(ResultCode.STOCK_NOT_ENOUGH);
        }

        int result = baseMapper.lockStock(dto.getWarehouseId(), dto.getSkuId(), dto.getQty());
        if (result <= 0) {
            throw new BusinessException(ResultCode.STOCK_NOT_ENOUGH);
        }

        int afterAvailable = beforeAvailable - dto.getQty();
        int afterLocked = stock.getLockedQty() + dto.getQty();

        stockLogService.addStockLog(
                dto.getWarehouseId(),
                dto.getSkuId(),
                StockConstants.STOCK_TYPE_LOCK,
                dto.getQty(),
                beforeAvailable,
                afterAvailable,
                dto.getOrderNo(),
                dto.getRemark()
        );

        evictStockCache(dto.getWarehouseId(), dto.getSkuId());

        return true;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    @DistributedLock(key = "#dto.warehouseId + '_' + #dto.skuId", prefix = "stock:lock:", expire = 10, timeUnit = java.util.concurrent.TimeUnit.SECONDS)
    public boolean releaseStock(StockLockDTO dto) {
        WarehouseStock stock = getStock(dto.getWarehouseId(), dto.getSkuId());
        if (stock == null) {
            throw new BusinessException("库存不存在");
        }

        int beforeLocked = stock.getLockedQty();
        if (beforeLocked < dto.getQty()) {
            throw new BusinessException("锁定库存不足");
        }

        int result = baseMapper.releaseStock(dto.getWarehouseId(), dto.getSkuId(), dto.getQty());
        if (result <= 0) {
            throw new BusinessException("释放库存失败");
        }

        int afterAvailable = stock.getAvailableQty() + dto.getQty();
        int afterLocked = beforeLocked - dto.getQty();

        stockLogService.addStockLog(
                dto.getWarehouseId(),
                dto.getSkuId(),
                StockConstants.STOCK_TYPE_RELEASE,
                dto.getQty(),
                beforeLocked,
                afterLocked,
                dto.getOrderNo(),
                dto.getRemark()
        );

        evictStockCache(dto.getWarehouseId(), dto.getSkuId());

        return true;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    @DistributedLock(key = "#dto.warehouseId + '_' + #dto.skuId", prefix = "stock:lock:", expire = 10, timeUnit = java.util.concurrent.TimeUnit.SECONDS)
    public boolean deductStock(StockLockDTO dto) {
        WarehouseStock stock = getStock(dto.getWarehouseId(), dto.getSkuId());
        if (stock == null) {
            throw new BusinessException("库存不存在");
        }

        int beforeLocked = stock.getLockedQty();
        if (beforeLocked < dto.getQty()) {
            throw new BusinessException("锁定库存不足");
        }

        int result = baseMapper.deductStock(dto.getWarehouseId(), dto.getSkuId(), dto.getQty());
        if (result <= 0) {
            throw new BusinessException("扣减库存失败");
        }

        int afterTotal = stock.getTotalQty() - dto.getQty();
        int afterLocked = beforeLocked - dto.getQty();

        stockLogService.addStockLog(
                dto.getWarehouseId(),
                dto.getSkuId(),
                StockConstants.STOCK_TYPE_DEDUCT,
                dto.getQty(),
                beforeLocked,
                afterLocked,
                dto.getOrderNo(),
                dto.getRemark()
        );

        evictStockCache(dto.getWarehouseId(), dto.getSkuId());

        return true;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    @DistributedLock(key = "#dto.warehouseId + '_' + #dto.skuId", prefix = "stock:lock:", expire = 10, timeUnit = java.util.concurrent.TimeUnit.SECONDS)
    public boolean restoreStock(StockLockDTO dto) {
        WarehouseStock stock = getStock(dto.getWarehouseId(), dto.getSkuId());
        if (stock == null) {
            initStock(dto.getWarehouseId(), dto.getSkuId(), 0);
            stock = getStock(dto.getWarehouseId(), dto.getSkuId());
        }

        int beforeAvailable = stock.getAvailableQty();
        int beforeTotal = stock.getTotalQty();

        int result = baseMapper.restoreStock(dto.getWarehouseId(), dto.getSkuId(), dto.getQty());
        if (result <= 0) {
            throw new BusinessException("回补库存失败");
        }

        int afterAvailable = beforeAvailable + dto.getQty();
        int afterTotal = beforeTotal + dto.getQty();

        stockLogService.addStockLog(
                dto.getWarehouseId(),
                dto.getSkuId(),
                StockConstants.STOCK_TYPE_RESTORE,
                dto.getQty(),
                beforeAvailable,
                afterAvailable,
                dto.getOrderNo(),
                dto.getRemark()
        );

        evictStockCache(dto.getWarehouseId(), dto.getSkuId());

        return true;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean stockIn(Long warehouseId, Long skuId, Integer qty, String orderNo, String remark) {
        WarehouseStock stock = getStock(warehouseId, skuId);
        if (stock == null) {
            initStock(warehouseId, skuId, qty);
            stockLogService.addStockLog(
                    warehouseId,
                    skuId,
                    StockConstants.STOCK_TYPE_IN,
                    qty,
                    0,
                    qty,
                    orderNo,
                    remark
            );
            return true;
        }

        int beforeAvailable = stock.getAvailableQty();
        int beforeTotal = stock.getTotalQty();

        int result = baseMapper.stockIn(warehouseId, skuId, qty);
        if (result <= 0) {
            throw new BusinessException("入库失败");
        }

        int afterAvailable = beforeAvailable + qty;
        int afterTotal = beforeTotal + qty;

        stockLogService.addStockLog(
                warehouseId,
                skuId,
                StockConstants.STOCK_TYPE_IN,
                qty,
                beforeAvailable,
                afterAvailable,
                orderNo,
                remark
        );

        evictStockCache(warehouseId, skuId);

        return true;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean stockOut(Long warehouseId, Long skuId, Integer qty, String orderNo, String remark) {
        WarehouseStock stock = getStock(warehouseId, skuId);
        if (stock == null) {
            throw new BusinessException("库存不存在");
        }

        int beforeAvailable = stock.getAvailableQty();
        if (beforeAvailable < qty) {
            throw new BusinessException(ResultCode.STOCK_NOT_ENOUGH);
        }

        int result = baseMapper.stockOut(warehouseId, skuId, qty);
        if (result <= 0) {
            throw new BusinessException("出库失败");
        }

        int afterAvailable = beforeAvailable - qty;
        int afterTotal = stock.getTotalQty() - qty;

        stockLogService.addStockLog(
                warehouseId,
                skuId,
                StockConstants.STOCK_TYPE_OUT,
                qty,
                beforeAvailable,
                afterAvailable,
                orderNo,
                remark
        );

        evictStockCache(warehouseId, skuId);

        return true;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean transferStock(StockTransferDTO dto) {
        if (dto.getFromWarehouseId().equals(dto.getToWarehouseId())) {
            throw new BusinessException("调出仓和调入仓不能相同");
        }

        StockLockDTO outDTO = new StockLockDTO();
        outDTO.setWarehouseId(dto.getFromWarehouseId());
        outDTO.setSkuId(dto.getSkuId());
        outDTO.setQty(dto.getQty());
        outDTO.setRemark("调拨出库：" + dto.getRemark());
        stockOut(dto.getFromWarehouseId(), dto.getSkuId(), dto.getQty(), null, "调拨出库");

        stockIn(dto.getToWarehouseId(), dto.getSkuId(), dto.getQty(), null, "调拨入库");

        return true;
    }

    @Override
    public void initStock(Long warehouseId, Long skuId, Integer qty) {
        WarehouseStock stock = getStock(warehouseId, skuId);
        if (stock != null) {
            return;
        }

        WarehouseStock newStock = new WarehouseStock();
        newStock.setWarehouseId(warehouseId);
        newStock.setSkuId(skuId);
        newStock.setAvailableQty(qty);
        newStock.setLockedQty(0);
        newStock.setInTransitQty(0);
        newStock.setTotalQty(qty);
        save(newStock);

        evictStockCache(warehouseId, skuId);
    }

    private WarehouseStock getStock(Long warehouseId, Long skuId) {
        return getOne(new LambdaQueryWrapper<WarehouseStock>()
                .eq(WarehouseStock::getWarehouseId, warehouseId)
                .eq(WarehouseStock::getSkuId, skuId));
    }

    private void evictStockCache(Long warehouseId, Long skuId) {
        String cacheKey = stockCachePrefix + warehouseId + ":" + skuId;
        redisUtil.delete(cacheKey);
    }
}
