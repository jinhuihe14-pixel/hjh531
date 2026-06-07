package com.fresh.order.strategy;

import com.fresh.order.dto.OrderItemDTO;
import com.fresh.order.feign.ProductFeignClient;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Component
@ConditionalOnProperty(name = "order.split.strategy", havingValue = "single-first", matchIfMissing = true)
public class SingleFirstSplitStrategy implements OrderSplitStrategy {

    @Resource
    private ProductFeignClient productFeignClient;

    @Override
    public SplitResult split(List<OrderItemDTO> items, BigDecimal longitude, BigDecimal latitude) {
        SplitResult result = new SplitResult();

        try {
            List<Map<String, Object>> nearbyWarehouses = getNearbyWarehouses(longitude, latitude);
            if (nearbyWarehouses.isEmpty()) {
                throw new RuntimeException("附近没有可用仓库");
            }

            for (Map<String, Object> warehouse : nearbyWarehouses) {
                Long warehouseId = Long.valueOf(warehouse.get("id").toString());
                String warehouseName = (String) warehouse.get("name");

                Map<Long, Integer> stockMap = getWarehouseStock(warehouseId, items);

                boolean canFulfillAll = true;
                for (OrderItemDTO item : items) {
                    Integer stock = stockMap.getOrDefault(item.getSkuId(), 0);
                    if (stock < item.getQuantity()) {
                        canFulfillAll = false;
                        break;
                    }
                }

                if (canFulfillAll) {
                    SplitResult.WarehouseAllocation allocation = new SplitResult.WarehouseAllocation();
                    allocation.setWarehouseId(warehouseId);
                    allocation.setWarehouseName(warehouseName);
                    for (OrderItemDTO item : items) {
                        SplitResult.SkuAllocation skuAllocation = new SplitResult.SkuAllocation();
                        skuAllocation.setSkuId(item.getSkuId());
                        skuAllocation.setQuantity(item.getQuantity());
                        allocation.getSkuAllocations().add(skuAllocation);
                    }
                    result.getWarehouseAllocations().add(allocation);
                    return result;
                }
            }

            result = splitByMultipleWarehouses(items, nearbyWarehouses);
            return result;

        } catch (Exception e) {
            log.error("拆单失败", e);
            throw new RuntimeException("拆单失败: " + e.getMessage());
        }
    }

    @Override
    public String getStrategyName() {
        return "single-first";
    }

    private List<Map<String, Object>> getNearbyWarehouses(BigDecimal longitude, BigDecimal latitude) {
        try {
            var response = productFeignClient.getNearbyWarehouses(longitude, latitude, 5);
            if (response != null && response.getCode() == 200 && response.getData() != null) {
                return response.getData();
            }
        } catch (Exception e) {
            log.warn("获取附近仓库失败，使用默认仓库", e);
        }
        List<Map<String, Object>> defaultWarehouses = new ArrayList<>();
        Map<String, Object> defaultWh = new HashMap<>();
        defaultWh.put("id", 1L);
        defaultWh.put("name", "默认仓库");
        defaultWarehouses.add(defaultWh);
        return defaultWarehouses;
    }

    private Map<Long, Integer> getWarehouseStock(Long warehouseId, List<OrderItemDTO> items) {
        Map<Long, Integer> stockMap = new HashMap<>();
        try {
            List<Long> skuIds = items.stream().map(OrderItemDTO::getSkuId).collect(Collectors.toList());
            var response = productFeignClient.getWarehouseBatchStock(
                    Collections.singletonList(warehouseId), skuIds);
            if (response != null && response.getCode() == 200 && response.getData() != null) {
                for (var stockInfo : response.getData()) {
                    Long skuId = Long.valueOf(stockInfo.get("skuId").toString());
                    Integer stock = Integer.valueOf(stockInfo.get("stock").toString());
                    stockMap.put(skuId, stock);
                }
            }
        } catch (Exception e) {
            log.warn("获取仓库库存失败，使用默认库存", e);
            for (OrderItemDTO item : items) {
                stockMap.put(item.getSkuId(), 999);
            }
        }
        return stockMap;
    }

    private SplitResult splitByMultipleWarehouses(List<OrderItemDTO> items,
                                                   List<Map<String, Object>> warehouses) {
        SplitResult result = new SplitResult();
        Map<Long, Integer> remainingQuantity = new HashMap<>();
        for (OrderItemDTO item : items) {
            remainingQuantity.put(item.getSkuId(), item.getQuantity());
        }

        for (Map<String, Object> warehouse : warehouses) {
            if (remainingQuantity.values().stream().allMatch(q -> q == 0)) {
                break;
            }

            Long warehouseId = Long.valueOf(warehouse.get("id").toString());
            String warehouseName = (String) warehouse.get("name");
            Map<Long, Integer> stockMap = getWarehouseStock(warehouseId, items);

            SplitResult.WarehouseAllocation allocation = new SplitResult.WarehouseAllocation();
            allocation.setWarehouseId(warehouseId);
            allocation.setWarehouseName(warehouseName);

            boolean hasAllocation = false;
            for (OrderItemDTO item : items) {
                Integer remaining = remainingQuantity.getOrDefault(item.getSkuId(), 0);
                if (remaining <= 0) {
                    continue;
                }
                Integer stock = stockMap.getOrDefault(item.getSkuId(), 0);
                if (stock <= 0) {
                    continue;
                }
                int allocateQty = Math.min(remaining, stock);
                SplitResult.SkuAllocation skuAllocation = new SplitResult.SkuAllocation();
                skuAllocation.setSkuId(item.getSkuId());
                skuAllocation.setQuantity(allocateQty);
                allocation.getSkuAllocations().add(skuAllocation);
                remainingQuantity.put(item.getSkuId(), remaining - allocateQty);
                hasAllocation = true;
            }

            if (hasAllocation) {
                result.getWarehouseAllocations().add(allocation);
            }
        }

        if (remainingQuantity.values().stream().anyMatch(q -> q > 0)) {
            throw new RuntimeException("部分商品库存不足，无法完成拆单");
        }

        return result;
    }

}
