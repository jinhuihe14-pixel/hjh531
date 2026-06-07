package com.fresh.settlement.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ResponsibleParty {

    SUPPLIER(1, "供应商"),
    WAREHOUSE(2, "仓库"),
    RIDER(3, "骑手"),
    PLATFORM(4, "平台");

    private final Integer code;
    private final String desc;

    public static ResponsibleParty getByCode(Integer code) {
        if (code == null) {
            return null;
        }
        for (ResponsibleParty party : values()) {
            if (party.getCode().equals(code)) {
                return party;
            }
        }
        return null;
    }

}
