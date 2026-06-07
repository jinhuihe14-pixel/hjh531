package com.fresh.aftersale.vo;

import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

@Data
public class AftersaleStatisticsVO implements Serializable {

    private Long totalCount;

    private Long pendingCount;

    private Long processingCount;

    private Long completedCount;

    private Long rejectedCount;

    private BigDecimal totalRefundAmount;

}
