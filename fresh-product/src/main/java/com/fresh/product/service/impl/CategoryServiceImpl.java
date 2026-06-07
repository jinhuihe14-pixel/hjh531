package com.fresh.product.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.fresh.common.exception.BusinessException;
import com.fresh.product.dto.CategoryDTO;
import com.fresh.product.entity.Category;
import com.fresh.product.mapper.CategoryMapper;
import com.fresh.product.service.CategoryService;
import com.fresh.product.vo.CategoryTreeVO;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class CategoryServiceImpl extends ServiceImpl<CategoryMapper, Category> implements CategoryService {

    @Override
    public List<CategoryTreeVO> tree() {
        List<Category> allCategories = list(new LambdaQueryWrapper<Category>()
                .eq(Category::getStatus, 1)
                .orderByAsc(Category::getSort));

        List<CategoryTreeVO> allVOs = allCategories.stream()
                .map(category -> BeanUtil.copyProperties(category, CategoryTreeVO.class))
                .collect(Collectors.toList());

        Map<Long, List<CategoryTreeVO>> childrenMap = allVOs.stream()
                .collect(Collectors.groupingBy(vo -> vo.getParentId() == null ? 0L : vo.getParentId()));

        List<CategoryTreeVO> roots = allVOs.stream()
                .filter(vo -> vo.getParentId() == null || vo.getParentId() == 0)
                .collect(Collectors.toList());

        buildTree(roots, childrenMap);

        return roots;
    }

    private void buildTree(List<CategoryTreeVO> nodes, Map<Long, List<CategoryTreeVO>> childrenMap) {
        for (CategoryTreeVO node : nodes) {
            List<CategoryTreeVO> children = childrenMap.get(node.getId());
            if (children != null && !children.isEmpty()) {
                node.setChildren(children);
                buildTree(children, childrenMap);
            }
        }
    }

    @Override
    public List<Category> list(Long parentId, Integer level, Integer status) {
        LambdaQueryWrapper<Category> wrapper = new LambdaQueryWrapper<>();
        if (parentId != null) {
            wrapper.eq(Category::getParentId, parentId);
        }
        if (level != null) {
            wrapper.eq(Category::getLevel, level);
        }
        if (status != null) {
            wrapper.eq(Category::getStatus, status);
        }
        wrapper.orderByAsc(Category::getSort);
        return list(wrapper);
    }

    @Override
    public void add(CategoryDTO dto) {
        Category category = BeanUtil.copyProperties(dto, Category.class);
        if (category.getSort() == null) {
            category.setSort(0);
        }
        if (category.getStatus() == null) {
            category.setStatus(1);
        }
        save(category);
    }

    @Override
    public void update(CategoryDTO dto) {
        if (dto.getId() == null) {
            throw new BusinessException("分类ID不能为空");
        }
        Category category = getById(dto.getId());
        if (category == null) {
            throw new BusinessException("分类不存在");
        }
        BeanUtil.copyProperties(dto, category);
        updateById(category);
    }

    @Override
    public void delete(Long id) {
        Category category = getById(id);
        if (category == null) {
            throw new BusinessException("分类不存在");
        }
        Long count = lambdaQuery().eq(Category::getParentId, id).count();
        if (count > 0) {
            throw new BusinessException("该分类下有子分类，不能删除");
        }
        removeById(id);
    }

    @Override
    public Category getById(Long id) {
        return super.getById(id);
    }
}
