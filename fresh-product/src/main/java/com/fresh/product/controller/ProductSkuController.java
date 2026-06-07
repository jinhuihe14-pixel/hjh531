package com.fresh.product.controller;

import com.fresh.common.result.R;
import com.fresh.product.entity.ProductSku;
import com.fresh.product.service.ProductSkuService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "商品SKU管理", description = "商品SKU相关接口")
@RestController
@RequestMapping("/product/sku")
public class ProductSkuController {

    @Resource
    private ProductSkuService productSkuService;

    @Operation(summary = "根据商品ID查询SKU列表")
    @GetMapping("/list")
    public R<List<ProductSku>> listByProductId(@RequestParam Long productId) {
        return R.success(productSkuService.listByProductId(productId));
    }

    @Operation(summary = "SKU详情")
    @GetMapping("/{id}")
    public R<ProductSku> getById(@PathVariable Long id) {
        return R.success(productSkuService.getById(id));
    }
}
