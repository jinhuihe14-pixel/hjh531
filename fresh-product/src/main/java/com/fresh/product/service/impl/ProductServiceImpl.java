package com.fresh.product.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.fresh.common.exception.BusinessException;
import com.fresh.common.page.PageQuery;
import com.fresh.product.dto.ProductDTO;
import com.fresh.product.dto.ProductSkuDTO;
import com.fresh.product.entity.Product;
import com.fresh.product.entity.ProductSku;
import com.fresh.product.mapper.ProductMapper;
import com.fresh.product.service.ProductService;
import com.fresh.product.service.ProductSkuService;
import com.fresh.product.vo.ProductDetailVO;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class ProductServiceImpl extends ServiceImpl<ProductMapper, Product> implements ProductService {

    @Resource
    private ProductSkuService productSkuService;

    @Override
    public IPage<Product> page(PageQuery pageQuery, Long categoryId, String keyword, Integer status) {
        LambdaQueryWrapper<Product> wrapper = new LambdaQueryWrapper<>();
        if (categoryId != null) {
            wrapper.eq(Product::getCategoryId, categoryId);
        }
        if (StrUtil.isNotBlank(keyword)) {
            wrapper.like(Product::getName, keyword);
        }
        if (status != null) {
            wrapper.eq(Product::getStatus, status);
        }
        wrapper.orderByDesc(Product::getSort)
                .orderByDesc(Product::getCreateTime);
        return page(new Page<>(pageQuery.getPageNum(), pageQuery.getPageSize()), wrapper);
    }

    @Override
    public ProductDetailVO detail(Long id) {
        Product product = getById(id);
        if (product == null) {
            throw new BusinessException("商品不存在");
        }
        ProductDetailVO vo = BeanUtil.copyProperties(product, ProductDetailVO.class);
        List<ProductSku> skuList = productSkuService.listByProductId(id);
        vo.setSkuList(skuList);
        return vo;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void add(ProductDTO dto) {
        Product product = BeanUtil.copyProperties(dto, Product.class);
        if (product.getStatus() == null) {
            product.setStatus(0);
        }
        if (product.getSort() == null) {
            product.setSort(0);
        }
        save(product);

        if (dto.getSkuList() != null && !dto.getSkuList().isEmpty()) {
            for (ProductSkuDTO skuDTO : dto.getSkuList()) {
                ProductSku sku = BeanUtil.copyProperties(skuDTO, ProductSku.class);
                sku.setProductId(product.getId());
                if (sku.getStatus() == null) {
                    sku.setStatus(1);
                }
                productSkuService.save(sku);
            }
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void update(ProductDTO dto) {
        if (dto.getId() == null) {
            throw new BusinessException("商品ID不能为空");
        }
        Product product = getById(dto.getId());
        if (product == null) {
            throw new BusinessException("商品不存在");
        }
        BeanUtil.copyProperties(dto, product);
        updateById(product);

        if (dto.getSkuList() != null) {
            productSkuService.remove(new LambdaQueryWrapper<ProductSku>()
                    .eq(ProductSku::getProductId, dto.getId()));
            for (ProductSkuDTO skuDTO : dto.getSkuList()) {
                ProductSku sku = BeanUtil.copyProperties(skuDTO, ProductSku.class);
                sku.setProductId(dto.getId());
                sku.setId(null);
                if (sku.getStatus() == null) {
                    sku.setStatus(1);
                }
                productSkuService.save(sku);
            }
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void delete(Long id) {
        Product product = getById(id);
        if (product == null) {
            throw new BusinessException("商品不存在");
        }
        removeById(id);
        productSkuService.remove(new LambdaQueryWrapper<ProductSku>()
                .eq(ProductSku::getProductId, id));
    }

    @Override
    public void onSale(Long id) {
        Product product = getById(id);
        if (product == null) {
            throw new BusinessException("商品不存在");
        }
        product.setStatus(1);
        updateById(product);
    }

    @Override
    public void offSale(Long id) {
        Product product = getById(id);
        if (product == null) {
            throw new BusinessException("商品不存在");
        }
        product.setStatus(0);
        updateById(product);
    }
}
