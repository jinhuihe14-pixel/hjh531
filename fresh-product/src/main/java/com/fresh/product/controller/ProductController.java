package com.fresh.product.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.fresh.common.page.PageQuery;
import com.fresh.common.result.R;
import com.fresh.product.dto.ProductDTO;
import com.fresh.product.entity.Product;
import com.fresh.product.service.ProductService;
import com.fresh.product.vo.ProductDetailVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

@Tag(name = "商品管理", description = "商品SPU相关接口")
@RestController
@RequestMapping("/product")
public class ProductController {

    @Resource
    private ProductService productService;

    @Operation(summary = "商品分页列表")
    @GetMapping("/page")
    public R<IPage<Product>> page(PageQuery pageQuery,
                                  @RequestParam(required = false) Long categoryId,
                                  @RequestParam(required = false) String keyword,
                                  @RequestParam(required = false) Integer status) {
        return R.success(productService.page(pageQuery, categoryId, keyword, status));
    }

    @Operation(summary = "商品列表")
    @GetMapping("/list")
    public R<IPage<Product>> list(PageQuery pageQuery,
                                  @RequestParam(required = false) Long categoryId,
                                  @RequestParam(required = false) String keyword) {
        return R.success(productService.page(pageQuery, categoryId, keyword, 1));
    }

    @Operation(summary = "商品详情")
    @GetMapping("/detail/{id}")
    public R<ProductDetailVO> detail(@PathVariable Long id) {
        return R.success(productService.detail(id));
    }

    @Operation(summary = "新增商品")
    @PostMapping
    public R<Void> add(@Valid @RequestBody ProductDTO dto) {
        productService.add(dto);
        return R.success();
    }

    @Operation(summary = "修改商品")
    @PutMapping
    public R<Void> update(@Valid @RequestBody ProductDTO dto) {
        productService.update(dto);
        return R.success();
    }

    @Operation(summary = "删除商品")
    @DeleteMapping("/{id}")
    public R<Void> delete(@PathVariable Long id) {
        productService.delete(id);
        return R.success();
    }

    @Operation(summary = "商品上架")
    @PostMapping("/onSale/{id}")
    public R<Void> onSale(@PathVariable Long id) {
        productService.onSale(id);
        return R.success();
    }

    @Operation(summary = "商品下架")
    @PostMapping("/offSale/{id}")
    public R<Void> offSale(@PathVariable Long id) {
        productService.offSale(id);
        return R.success();
    }
}
