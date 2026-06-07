package com.fresh.product.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.fresh.common.entity.BaseEntity;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("warehouse_stock")
@Schema(description = "仓库库存")
public class WarehouseStock extends BaseEntity {

    @Schema(description = "仓库ID")
    private Long warehouseId;

    @Schema(description = "SKU ID")
    private Long skuId;

    @Schema(description = "可售库存")
    private Integer availableQty;

    @Schema(description = "锁定库存")
    private Integer lockedQty;

    @Schema(description = "在途库存")
    private Integer inTransitQty;

    @Schema(description = "合计库存")
    private Integer totalQty;
}
