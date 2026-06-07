package com.fresh.settlement.vo;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class RiderSettlementVO {

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

    private String statusDesc;

    private LocalDateTime createTime;

}
