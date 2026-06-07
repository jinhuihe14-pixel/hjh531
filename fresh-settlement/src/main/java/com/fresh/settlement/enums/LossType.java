package com.fresh.settlement.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum LossType {

    EXPIRED(1, "临期"),
    DAMAGED(2, "破损"),
    REJECTED(3, "拒收");

    private final Integer code;
    private final String desc;

    public static LossType getByCode(Integer code) {
        if (code == null) {
            return null;
        }
        for (LossType type : values()) {
            if (type.getCode().equals(code)) {
                return type;
            }
        }
        return null;
    }

}
