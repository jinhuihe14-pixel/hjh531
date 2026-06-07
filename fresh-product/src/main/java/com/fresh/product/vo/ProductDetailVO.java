package com.fresh.product.vo;

import com.fresh.product.entity.ProductSku;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

@Data
@Schema(description = "商品详情VO")
public class ProductDetailVO {

    @Schema(description = "商品ID")
    private Long id;

    @Schema(description = "分类ID")
    private Long categoryId;

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

    @Schema(description = "存储类型：1-常温，2-冷藏，3-冷冻")
    private Integer storageType;

    @Schema(description = "状态")
    private Integer status;

    @Schema(description = "排序")
    private Integer sort;

    @Schema(description = "SKU列表")
    private List<ProductSku> skuList;
}
