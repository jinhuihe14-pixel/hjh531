package com.fresh.settlement.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum RiderSettlementStatus {

    PENDING(1, "待结算"),
    CONFIRMED(2, "已确认"),
    PAID(3, "已付款");

    private final Integer code;
    private final String desc;

    public static RiderSettlementStatus getByCode(Integer code) {
        if (code == null) {
            return null;
        }
        for (RiderSettlementStatus status : values()) {
            if (status.getCode().equals(code)) {
                return status;
            }
        }
        return null;
    }

}
