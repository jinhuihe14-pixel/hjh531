package com.fresh.product.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
@Schema(description = "库存锁定DTO")
public class StockLockDTO {

    @NotNull(message = "仓库ID不能为空")
    @Schema(description = "仓库ID")
    private Long warehouseId;

    @NotNull(message = "SKU ID不能为空")
    @Schema(description = "SKU ID")
    private Long skuId;

    @NotNull(message = "数量不能为空")
    @Schema(description = "数量")
    private Integer qty;

    @Schema(description = "订单号")
    private String orderNo;

    @Schema(description = "备注")
    private String remark;
}
