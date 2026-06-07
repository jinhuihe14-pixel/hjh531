package com.fresh.order.feign;

import com.fresh.common.result.R;
import org.springframework.cloud.openfeign.FeignClient;

import java.math.BigDecimal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@FeignClient(name = "fresh-product", path = "/product")
public interface ProductFeignClient {

    @GetMapping("/sku/{skuId}")
    R<Map<String, Object>> getSkuInfo(@PathVariable("skuId") Long skuId);

    @PostMapping("/stock/preDeduct")
    R<Boolean> preDeductStock(@RequestParam("skuId") Long skuId,
                              @RequestParam("quantity") Integer quantity,
                              @RequestParam("orderNo") String orderNo);

    @PostMapping("/stock/batchPreDeduct")
    R<Boolean> batchPreDeductStock(@RequestBody List<Map<String, Object>> items,
                                   @RequestParam("orderNo") String orderNo);

    @PostMapping("/stock/confirmDeduct")
    R<Boolean> confirmDeductStock(@RequestParam("orderNo") String orderNo);

    @PostMapping("/stock/release")
    R<Boolean> releaseStock(@RequestParam("orderNo") String orderNo);

    @GetMapping("/warehouse/nearby")
    R<List<Map<String, Object>>> getNearbyWarehouses(@RequestParam("longitude") BigDecimal longitude,
                                                      @RequestParam("latitude") BigDecimal latitude,
                                                      @RequestParam(value = "radius", defaultValue = "5") Integer radius);

    @GetMapping("/warehouse/stock")
    R<Map<String, Object>> getWarehouseStock(@RequestParam("warehouseId") Long warehouseId,
                                              @RequestParam("skuId") Long skuId);

    @GetMapping("/warehouse/batchStock")
    R<List<Map<String, Object>>> getWarehouseBatchStock(@RequestParam("warehouseIds") List<Long> warehouseIds,
                                                         @RequestParam("skuIds") List<Long> skuIds);

}
