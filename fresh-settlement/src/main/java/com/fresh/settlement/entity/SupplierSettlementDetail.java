package com.fresh.settlement.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@TableName("supplier_settlement_detail")
public class SupplierSettlementDetail implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(type = IdType.AUTO)
    private Long id;

    private Long settlementId;

    private Long skuId;

    private String skuName;

    private BigDecimal actualQty;

    private BigDecimal lossRatio;

    private BigDecimal lossQty;

    private BigDecimal unitPrice;

    private BigDecimal totalAmount;

    private BigDecimal deductionAmount;

    private BigDecimal actualAmount;

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
