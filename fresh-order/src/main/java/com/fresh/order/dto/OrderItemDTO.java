package com.fresh.order.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class OrderItemDTO {

    @NotNull(message = "商品SKU ID不能为空")
    private Long skuId;

    private String skuName;

    private String skuImage;

    private BigDecimal price;

    @NotNull(message = "购买数量不能为空")
    @Min(value = 1, message = "购买数量不能小于1")
    private Integer quantity;

    private Integer storageType;

}
