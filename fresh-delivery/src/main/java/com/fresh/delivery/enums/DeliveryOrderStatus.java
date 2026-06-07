package com.fresh.delivery.enums;

import lombok.Getter;

@Getter
public enum DeliveryOrderStatus {

    PENDING_ACCEPT(1, "待接单"),
    ACCEPTED(2, "已接单"),
    PICKING_UP(3, "取货中"),
    DELIVERING(4, "配送中"),
    COMPLETED(5, "已完成"),
    CANCELLED(6, "已取消");

    private final Integer code;
    private final String desc;

    DeliveryOrderStatus(Integer code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    public static String getDesc(Integer code) {
        if (code == null) {
            return "";
        }
        for (DeliveryOrderStatus status : values()) {
            if (status.getCode().equals(code)) {
                return status.getDesc();
            }
        }
        return "";
    }
}
