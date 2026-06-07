package com.fresh.aftersale.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum AftersaleTypeEnum {

    REFUND(1, "退款"),
    EXCHANGE(2, "换货"),
    RESEND(3, "补发"),
    PARTIAL_REFUND(4, "部分退款"),
    FULL_REFUND(5, "整单退款"),
    REJECT_REFUND(6, "拒收退款"),
    SPOILED_REFUND(7, "商品变质退款"),
    MISSING_RESEND(8, "少货补发"),
    MISSING_REFUND(9, "少货退款");

    private final Integer code;
    private final String desc;

}
