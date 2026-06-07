package com.fresh.settlement.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class SupplierSettlementGenerateDTO {

    @NotNull(message = "供应商ID不能为空")
    private Long supplierId;

    @NotNull(message = "结算开始时间不能为空")
    private LocalDateTime periodStart;

    @NotNull(message = "结算结束时间不能为空")
    private LocalDateTime periodEnd;

}
