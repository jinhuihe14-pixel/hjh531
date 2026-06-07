package com.fresh.warehouse.enums;

import lombok.Getter;

@Getter
public enum StaffRoleEnum {

    WAREHOUSE_MANAGER(1, "仓管"),
    SORTER(2, "分拣员"),
    DELIVERY(3, "配送员");

    private final Integer code;
    private final String desc;

    StaffRoleEnum(Integer code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    public static StaffRoleEnum getByCode(Integer code) {
        for (StaffRoleEnum role : values()) {
            if (role.getCode().equals(code)) {
                return role;
            }
        }
        return null;
    }
}
