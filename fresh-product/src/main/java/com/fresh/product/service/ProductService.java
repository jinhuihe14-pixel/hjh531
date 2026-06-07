package com.fresh.product.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.fresh.common.page.PageQuery;
import com.fresh.product.dto.ProductDTO;
import com.fresh.product.entity.Product;
import com.fresh.product.vo.ProductDetailVO;

import java.util.List;

public interface ProductService extends IService<Product> {

    IPage<Product> page(PageQuery pageQuery, Long categoryId, String keyword, Integer status);

    ProductDetailVO detail(Long id);

    void add(ProductDTO dto);

    void update(ProductDTO dto);

    void delete(Long id);

    void onSale(Long id);

    void offSale(Long id);
}
