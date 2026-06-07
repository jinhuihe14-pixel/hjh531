package com.fresh.product.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.fresh.common.entity.BaseEntity;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("product_sku")
@Schema(description = "商品SKU")
public class ProductSku extends BaseEntity {

    @Schema(description = "商品SPU ID")
    private Long productId;

    @Schema(description = "SKU名称")
    private String skuName;

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
