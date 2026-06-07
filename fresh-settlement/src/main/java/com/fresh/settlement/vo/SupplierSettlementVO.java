package com.fresh.settlement.vo;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class SupplierSettlementVO {

    private Long id;

    private String settlementNo;

    private Long supplierId;

    private String supplierName;

    private LocalDateTime periodStart;

    private LocalDateTime periodEnd;

    private BigDecimal totalAmount;

    private BigDecimal deductionAmount;

    private BigDecimal actualAmount;

    private Integer status;

    private String statusDesc;

    private LocalDateTime settlementTime;

    private LocalDateTime createTime;

    private List<SupplierSettlementDetailVO> details;

}
