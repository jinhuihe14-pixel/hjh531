package com.fresh.product.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.fresh.common.entity.BaseEntity;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("product")
@Schema(description = "商品SPU")
public class Product extends BaseEntity {

    @Schema(description = "分类ID")
    private Long categoryId;

    @Schema(description = "商品名称")
    private String name;

    @Schema(description = "副标题")
    private String subtitle;

    @Schema(description = "主图")
    private String mainImage;

    @Schema(description = "轮播图，多个用逗号分隔")
    private String images;

    @Schema(description = "商品描述")
    private String description;

    @Schema(description = "存储类型：1-常温，2-冷藏，3-冷冻")
    private Integer storageType;

    @Schema(description = "状态：0-下架，1-上架")
    private Integer status;

    @Schema(description = "排序")
    private Integer sort;
}
