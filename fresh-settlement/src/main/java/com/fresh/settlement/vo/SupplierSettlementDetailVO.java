package com.fresh.settlement.vo;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class SupplierSettlementDetailVO {

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

}
