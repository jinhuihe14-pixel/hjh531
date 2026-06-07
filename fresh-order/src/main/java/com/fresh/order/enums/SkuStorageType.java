package com.fresh.order.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum SkuStorageType {

    NORMAL(1, "常温"),
    REFRIGERATED(2, "冷藏"),
    FROZEN(3, "冷冻");

    private final Integer code;
    private final String desc;

    public static SkuStorageType getByCode(Integer code) {
        if (code == null) {
            return null;
        }
        for (SkuStorageType type : values()) {
            if (type.getCode().equals(code)) {
                return type;
            }
        }
        return null;
    }

}
