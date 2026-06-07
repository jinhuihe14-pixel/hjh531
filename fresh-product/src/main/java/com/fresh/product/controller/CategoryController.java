package com.fresh.product.controller;

import com.fresh.common.result.R;
import com.fresh.product.dto.CategoryDTO;
import com.fresh.product.entity.Category;
import com.fresh.product.service.CategoryService;
import com.fresh.product.vo.CategoryTreeVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "商品分类管理", description = "商品分类相关接口")
@RestController
@RequestMapping("/category")
public class CategoryController {

    @Resource
    private CategoryService categoryService;

    @Operation(summary = "分类树")
    @GetMapping("/tree")
    public R<List<CategoryTreeVO>> tree() {
        return R.success(categoryService.tree());
    }

    @Operation(summary = "分类列表")
    @GetMapping("/list")
    public R<List<Category>> list(
            @RequestParam(required = false) Long parentId,
            @RequestParam(required = false) Integer level,
            @RequestParam(required = false) Integer status) {
        return R.success(categoryService.list(parentId, level, status));
    }

    @Operation(summary = "分类详情")
    @GetMapping("/{id}")
    public R<Category> getById(@PathVariable Long id) {
        return R.success(categoryService.getById(id));
    }

    @Operation(summary = "新增分类")
    @PostMapping
    public R<Void> add(@Valid @RequestBody CategoryDTO dto) {
        categoryService.add(dto);
        return R.success();
    }

    @Operation(summary = "修改分类")
    @PutMapping
    public R<Void> update(@Valid @RequestBody CategoryDTO dto) {
        categoryService.update(dto);
        return R.success();
    }

    @Operation(summary = "删除分类")
    @DeleteMapping("/{id}")
    public R<Void> delete(@PathVariable Long id) {
        categoryService.delete(id);
        return R.success();
    }
}
