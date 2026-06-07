package com.fresh.product.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
@Schema(description = "商品DTO")
public class ProductDTO {

    @Schema(description = "商品ID")
    private Long id;

    @NotNull(message = "分类ID不能为空")
    @Schema(description = "分类ID")
    private Long categoryId;

    @NotBlank(message = "商品名称不能为空")
    @Schema(description = "商品名称")
    private String name;

    @Schema(description = "副标题")
    private String subtitle;

    @Schema(description = "主图")
    private String mainImage;

    @Schema(description = "轮播图")
    private String images;

    @Schema(description = "商品描述")
    private String description;

    @NotNull(message = "存储类型不能为空")
    @Schema(description = "存储类型：1-常温，2-冷藏，3-冷冻")
    private Integer storageType;

    @Schema(description = "状态：0-下架，1-上架")
    private Integer status;

    @Schema(description = "排序")
    private Integer sort;

    @Schema(description = "SKU列表")
    private List<ProductSkuDTO> skuList;
}
