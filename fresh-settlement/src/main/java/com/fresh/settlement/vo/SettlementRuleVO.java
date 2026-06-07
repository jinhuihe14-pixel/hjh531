package com.fresh.settlement.vo;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class SettlementRuleVO {

    private Long id;

    private Integer ruleType;

    private String ruleTypeDesc;

    private String ruleName;

    private String ruleContent;

    private Integer version;

    private Integer status;

    private LocalDateTime effectiveTime;

    private LocalDateTime expireTime;

    private LocalDateTime createTime;

}
