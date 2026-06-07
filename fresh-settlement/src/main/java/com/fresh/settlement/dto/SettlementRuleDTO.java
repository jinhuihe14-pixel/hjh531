package com.fresh.settlement.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class SettlementRuleDTO {

    @NotNull(message = "规则类型不能为空")
    private Integer ruleType;

    @NotBlank(message = "规则名称不能为空")
    private String ruleName;

    @NotBlank(message = "规则内容不能为空")
    private String ruleContent;

    private Integer version;

    private Integer status;

    @NotNull(message = "生效时间不能为空")
    private LocalDateTime effectiveTime;

    private LocalDateTime expireTime;

}
