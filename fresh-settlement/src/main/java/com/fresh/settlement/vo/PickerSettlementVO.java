package com.fresh.settlement.vo;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class PickerSettlementVO {

    private Long id;

    private String settlementNo;

    private Long pickerId;

    private String pickerName;

    private LocalDateTime periodStart;

    private LocalDateTime periodEnd;

    private Integer totalPieces;

    private BigDecimal baseAmount;

    private BigDecimal subsidy;

    private BigDecimal totalAmount;

    private Integer status;

    private String statusDesc;

    private LocalDateTime createTime;

}
