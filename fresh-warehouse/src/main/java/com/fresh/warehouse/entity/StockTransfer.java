package com.fresh.warehouse.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.fresh.common.entity.BaseEntity;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("stock_transfer")
@Schema(description = "库存调拨单")
public class StockTransfer extends BaseEntity {

    @Schema(description = "调拨单号")
    private String transferNo;

    @Schema(description = "调出仓ID")
    private Long fromWarehouseId;

    @Schema(description = "调入仓ID")
    private Long toWarehouseId;

    @Schema(description = "状态：1-待审批，2-已批准，3-已驳回，4-调拨中，5-已完成")
    private Integer status;

    @Schema(description = "申请人ID")
    private Long applyUserId;

    @Schema(description = "审批人ID")
    private Long approveUserId;

    @Schema(description = "审批时间")
    private LocalDateTime approveTime;

    @Schema(description = "备注")
    private String remark;
}
