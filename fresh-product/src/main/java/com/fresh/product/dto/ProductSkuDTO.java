package com.fresh.product.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Schema(description = "商品SKU DTO")
public class ProductSkuDTO {

    @Schema(description = "SKU ID")
    private Long id;

    @Schema(description = "商品SPU ID")
    private Long productId;

    @NotBlank(message = "SKU名称不能为空")
    @Schema(description = "SKU名称")
    private String skuName;

    @NotNull(message = "销售价不能为空")
    @Schema(description = "销售价")
    private BigDecimal price;

    @Schema(description = "原价")
    private BigDecimal originalPrice;

    @Schema(description = "单位")
    private String unit;

    @Schema(description = "规格")
    private String spec;

    @Schema(description = "重量（克）")
    private Integer weight;

    @Schema(description = "条形码")
    private String barCode;

    @Schema(description = "状态：0-禁用，1-启用")
    private Integer status;
}
