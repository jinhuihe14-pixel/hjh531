package com.fresh.warehouse.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.List;

@Data
@Schema(description = "创建调拨申请DTO")
public class StockTransferCreateDTO {

    @NotNull(message = "调出仓ID不能为空")
    @Schema(description = "调出仓ID")
    private Long fromWarehouseId;

    @NotNull(message = "调入仓ID不能为空")
    @Schema(description = "调入仓ID")
    private Long toWarehouseId;

    @NotEmpty(message = "调拨明细不能为空")
    @Schema(description = "调拨明细列表")
    private List<StockTransferItemDTO> items;

    @Schema(description = "备注")
    private String remark;
}
