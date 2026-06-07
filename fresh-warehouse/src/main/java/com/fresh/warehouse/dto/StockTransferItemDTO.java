package com.fresh.warehouse.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
@Schema(description = "调拨明细DTO")
public class StockTransferItemDTO {

    @NotNull(message = "SKU ID不能为空")
    @Schema(description = "SKU ID")
    private Long skuId;

    @NotNull(message = "调拨数量不能为空")
    @Schema(description = "调拨数量")
    private Integer qty;
}
