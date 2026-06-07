package com.fresh.settlement.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum SettlementCycle {

    DAILY(1, "日结"),
    WEEKLY(2, "周结"),
    MONTHLY(3, "月结");

    private final Integer code;
    private final String desc;

    public static SettlementCycle getByCode(Integer code) {
        if (code == null) {
            return null;
        }
        for (SettlementCycle cycle : values()) {
            if (cycle.getCode().equals(code)) {
                return cycle;
            }
        }
        return null;
    }

}
