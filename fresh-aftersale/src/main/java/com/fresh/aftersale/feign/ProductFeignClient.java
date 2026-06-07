package com.fresh.aftersale.feign;

import com.fresh.common.result.R;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@FeignClient(name = "fresh-product", path = "/product")
public interface ProductFeignClient {

    @PostMapping("/stock/restore")
    R<Boolean> restoreStock(@RequestParam("skuId") Long skuId,
                            @RequestParam("quantity") Integer quantity,
                            @RequestParam("warehouseId") Long warehouseId,
                            @RequestParam("reason") String reason);

    @PostMapping("/stock/batchRestore")
    R<Boolean> batchRestoreStock(@RequestBody List<Map<String, Object>> items,
                                 @RequestParam("aftersaleNo") String aftersaleNo);

    @GetMapping("/sku/{skuId}")
    R<Map<String, Object>> getSkuInfo(@PathVariable("skuId") Long skuId);

}
