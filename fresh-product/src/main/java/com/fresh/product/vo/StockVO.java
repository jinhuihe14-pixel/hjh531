package com.fresh.product.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "库存VO")
public class StockVO {

    @Schema(description = "仓库ID")
    private Long warehouseId;

    @Schema(description = "仓库名称")
    private String warehouseName;

    @Schema(description = "SKU ID")
    private Long skuId;

    @Schema(description = "SKU名称")
    private String skuName;

    @Schema(description = "可售库存")
    private Integer availableQty;

    @Schema(description = "锁定库存")
    private Integer lockedQty;

    @Schema(description = "在途库存")
    private Integer inTransitQty;

    @Schema(description = "合计库存")
    private Integer totalQty;
}
