package com.fresh.settlement.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum SupplierSettlementStatus {

    PENDING_RECONCILIATION(1, "待对账"),
    CONFIRMED(2, "已确认"),
    PAID(3, "已付款");

    private final Integer code;
    private final String desc;

    public static SupplierSettlementStatus getByCode(Integer code) {
        if (code == null) {
            return null;
        }
        for (SupplierSettlementStatus status : values()) {
            if (status.getCode().equals(code)) {
                return status;
            }
        }
        return null;
    }

}
