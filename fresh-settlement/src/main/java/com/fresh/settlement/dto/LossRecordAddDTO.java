package com.fresh.settlement.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class LossRecordAddDTO {

    @NotNull(message = "损耗类型不能为空")
    private Integer type;

    @NotNull(message = "SKU ID不能为空")
    private Long skuId;

    @NotBlank(message = "SKU名称不能为空")
    private String skuName;

    @NotNull(message = "损耗数量不能为空")
    private BigDecimal qty;

    @NotNull(message = "损耗金额不能为空")
    private BigDecimal amount;

    @NotNull(message = "责任方不能为空")
    private Integer responsibleParty;

    private String remark;

}
