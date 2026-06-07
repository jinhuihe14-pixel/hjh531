package com.fresh.warehouse.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.fresh.common.entity.BaseEntity;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("stock_transfer_item")
@Schema(description = "库存调拨明细")
public class StockTransferItem extends BaseEntity {

    @Schema(description = "调拨单ID")
    private Long transferId;

    @Schema(description = "调拨单号")
    private String transferNo;

    @Schema(description = "SKU ID")
    private Long skuId;

    @Schema(description = "调拨数量")
    private Integer qty;
}
