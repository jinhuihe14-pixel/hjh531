package com.fresh.aftersale.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum AftersaleStatusEnum {

    PENDING_REVIEW(0, "待审核"),
    PROCESSING(1, "处理中"),
    COMPLETED(2, "已完成"),
    REJECTED(3, "已驳回"),
    CANCELLED(4, "已取消"),
    PENDING_APPROVAL(5, "待审批"),
    REFUNDING(6, "退款中");

    private final Integer code;
    private final String desc;

}
