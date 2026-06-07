package com.fresh.delivery.enums;

import lombok.Getter;

@Getter
public enum RiderWorkStatus {

    IDLE(1, "空闲"),
    DELIVERING(2, "配送中"),
    OFFLINE(3, "离线");

    private final Integer code;
    private final String desc;

    RiderWorkStatus(Integer code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    public static String getDesc(Integer code) {
        if (code == null) {
            return "";
        }
        for (RiderWorkStatus status : values()) {
            if (status.getCode().equals(code)) {
                return status.getDesc();
            }
        }
        return "";
    }
}
