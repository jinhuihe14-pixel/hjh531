package com.fresh.warehouse.vo;

import com.fresh.warehouse.entity.StockTransfer;
import com.fresh.warehouse.entity.StockTransferItem;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

@Data
@EqualsAndHashCode(callSuper = true)
@Schema(description = "调拨单详情VO")
public class StockTransferDetailVO extends StockTransfer {

    @Schema(description = "调出仓名称")
    private String fromWarehouseName;

    @Schema(description = "调入仓名称")
    private String toWarehouseName;

    @Schema(description = "调拨明细列表")
    private List<StockTransferItem> items;
}
