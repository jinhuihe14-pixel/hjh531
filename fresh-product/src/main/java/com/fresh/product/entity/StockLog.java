package com.fresh.product.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.fresh.common.entity.BaseEntity;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("stock_log")
@Schema(description = "库存流水")
public class StockLog extends BaseEntity {

    @Schema(description = "仓库ID")
    private Long warehouseId;

    @Schema(description = "SKU ID")
    private Long skuId;

    @Schema(description = "流水类型：1-入库，2-出库，3-锁定，4-释放，5-扣减，6-回补，7-调拨入，8-调拨出")
    private Integer type;

    @Schema(description = "变动数量")
    private Integer qty;

    @Schema(description = "变动前库存")
    private Integer beforeQty;

    @Schema(description = "变动后库存")
    private Integer afterQty;

    @Schema(description = "关联订单号")
    private String orderNo;

    @Schema(description = "备注")
    private String remark;
}
