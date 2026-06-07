package com.fresh.warehouse.feign;

import com.fresh.common.result.R;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

@FeignClient(name = "fresh-product", path = "/product")
public interface ProductFeignClient {

    @PostMapping("/stock/out")
    R<Boolean> stockOut(@RequestParam("warehouseId") Long warehouseId,
                        @RequestParam("skuId") Long skuId,
                        @RequestParam("qty") Integer qty,
                        @RequestParam(value = "orderNo", required = false) String orderNo,
                        @RequestParam(value = "remark", required = false) String remark);

    @PostMapping("/stock/in")
    R<Boolean> stockIn(@RequestParam("warehouseId") Long warehouseId,
                       @RequestParam("skuId") Long skuId,
                       @RequestParam("qty") Integer qty,
                       @RequestParam(value = "orderNo", required = false) String orderNo,
                       @RequestParam(value = "remark", required = false) String remark);

    @GetMapping("/stock/detail")
    R<Object> getStockDetail(@RequestParam("warehouseId") Long warehouseId,
                             @RequestParam("skuId") Long skuId);
}
