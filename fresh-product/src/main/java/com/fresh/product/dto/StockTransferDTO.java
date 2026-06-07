package com.fresh.product.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
@Schema(description = "库存调拨DTO")
public class StockTransferDTO {

    @NotNull(message = "调出仓库ID不能为空")
    @Schema(description = "调出仓库ID")
    private Long fromWarehouseId;

    @NotNull(message = "调入仓库ID不能为空")
    @Schema(description = "调入仓库ID")
    private Long toWarehouseId;

    @NotNull(message = "SKU ID不能为空")
    @Schema(description = "SKU ID")
    private Long skuId;

    @NotNull(message = "数量不能为空")
    @Schema(description = "数量")
    private Integer qty;

    @Schema(description = "备注")
    private String remark;
}
