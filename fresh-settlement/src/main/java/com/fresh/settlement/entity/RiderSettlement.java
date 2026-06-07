package com.fresh.settlement.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@TableName("rider_settlement")
public class RiderSettlement implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(type = IdType.AUTO)
    private Long id;

    private String settlementNo;

    private Long riderId;

    private String riderName;

    private LocalDateTime periodStart;

    private LocalDateTime periodEnd;

    private Integer orderCount;

    private BigDecimal totalDistance;

    private BigDecimal baseAmount;

    private BigDecimal distanceAmount;

    private BigDecimal timeSubsidy;

    private BigDecimal penaltyAmount;

    private BigDecimal totalAmount;

    private Integer status;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;

    @TableField(fill = FieldFill.INSERT)
    private Long createBy;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private Long updateBy;

    @TableLogic
    @TableField(fill = FieldFill.INSERT)
    private Integer deleted;

}
