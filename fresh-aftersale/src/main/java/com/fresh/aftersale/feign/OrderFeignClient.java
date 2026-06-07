package com.fresh.aftersale.feign;

import com.fresh.common.result.R;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@FeignClient(name = "fresh-order", path = "/order")
public interface OrderFeignClient {

    @GetMapping("/{orderNo}")
    R<Map<String, Object>> getOrderByNo(@PathVariable("orderNo") String orderNo);

    @GetMapping("/items/{orderNo}")
    R<java.util.List<Map<String, Object>>> getOrderItems(@PathVariable("orderNo") String orderNo);

}
