package com.fresh.product.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.fresh.common.entity.BaseEntity;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("category")
@Schema(description = "商品分类")
public class Category extends BaseEntity {

    @Schema(description = "分类名称")
    private String name;

    @Schema(description = "父级分类ID")
    private Long parentId;

    @Schema(description = "层级：1-一级分类，2-二级分类，3-三级分类")
    private Integer level;

    @Schema(description = "排序")
    private Integer sort;

    @Schema(description = "图标")
    private String icon;

    @Schema(description = "状态：0-禁用，1-启用")
    private Integer status;
}
