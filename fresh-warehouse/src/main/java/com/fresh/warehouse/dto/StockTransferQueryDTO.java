package com.fresh.warehouse.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "调拨单查询DTO")
public class StockTransferQueryDTO {

    @Schema(description = "调拨单号")
    private String transferNo;

    @Schema(description = "调出仓ID")
    private Long fromWarehouseId;

    @Schema(description = "调入仓ID")
    private Long toWarehouseId;

    @Schema(description = "状态")
    private Integer status;
}
