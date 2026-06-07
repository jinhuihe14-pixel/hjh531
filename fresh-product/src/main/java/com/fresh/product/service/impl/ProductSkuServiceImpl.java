package com.fresh.product.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.fresh.product.entity.ProductSku;
import com.fresh.product.mapper.ProductSkuMapper;
import com.fresh.product.service.ProductSkuService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProductSkuServiceImpl extends ServiceImpl<ProductSkuMapper, ProductSku> implements ProductSkuService {

    @Override
    public List<ProductSku> listByProductId(Long productId) {
        return list(new LambdaQueryWrapper<ProductSku>()
                .eq(ProductSku::getProductId, productId)
                .eq(ProductSku::getStatus, 1)
                .orderByAsc(ProductSku::getPrice));
    }

    @Override
    public List<ProductSku> listByProductIds(List<Long> productIds) {
        return list(new LambdaQueryWrapper<ProductSku>()
                .in(ProductSku::getProductId, productIds)
                .eq(ProductSku::getStatus, 1));
    }

    @Override
    public ProductSku getById(Long id) {
        return super.getById(id);
    }
}
