package com.fresh.aftersale.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ApprovalLevelEnum {

    LEVEL_1(1, "客服直接处理", "small"),
    LEVEL_2(2, "主管审批", "medium"),
    LEVEL_3(3, "经理审批", "large");

    private final Integer level;
    private final String desc;
    private final String type;

}
