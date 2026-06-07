package com.fresh.aftersale.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum AftersaleReasonEnum {

    QUALITY_ISSUE(1, "质量问题"),
    SPOILED(2, "商品变质"),
    MISSING_ITEM(3, "少发商品"),
    WRONG_ITEM(4, "发错商品"),
    DAMAGED(5, "商品损坏"),
    REJECTED(6, "拒收"),
    OTHER(7, "其他");

    private final Integer code;
    private final String desc;

}
