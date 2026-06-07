package com.fresh.product.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.fresh.product.dto.CategoryDTO;
import com.fresh.product.entity.Category;
import com.fresh.product.vo.CategoryTreeVO;

import java.util.List;

public interface CategoryService extends IService<Category> {

    List<CategoryTreeVO> tree();

    List<Category> list(Long parentId, Integer level, Integer status);

    void add(CategoryDTO dto);

    void update(CategoryDTO dto);

    void delete(Long id);

    Category getById(Long id);
}
