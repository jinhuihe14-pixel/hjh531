package com.fresh.product.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.fresh.product.entity.ProductSku;

import java.util.List;

public interface ProductSkuService extends IService<ProductSku> {

    List<ProductSku> listByProductId(Long productId);

    List<ProductSku> listByProductIds(List<Long> productIds);

    ProductSku getById(Long id);
}
