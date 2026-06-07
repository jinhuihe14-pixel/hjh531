package com.fresh.delivery.enums;

import lombok.Getter;

@Getter
public enum RiderStatus {

    ON_JOB(1, "在职"),
    OFF_JOB(2, "离职"),
    REST(3, "休息");

    private final Integer code;
    private final String desc;

    RiderStatus(Integer code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    public static String getDesc(Integer code) {
        if (code == null) {
            return "";
        }
        for (RiderStatus status : values()) {
            if (status.getCode().equals(code)) {
                return status.getDesc();
            }
        }
        return "";
    }
}
