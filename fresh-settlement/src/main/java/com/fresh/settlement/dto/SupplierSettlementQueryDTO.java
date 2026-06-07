package com.fresh.settlement.dto;

import com.fresh.common.page.PageQuery;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class SupplierSettlementQueryDTO extends PageQuery {

    private String settlementNo;

    private Long supplierId;

    private Integer status;

    private LocalDateTime startTime;

    private LocalDateTime endTime;

}
