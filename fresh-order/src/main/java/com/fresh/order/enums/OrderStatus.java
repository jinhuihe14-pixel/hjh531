package com.fresh.order.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum OrderStatus {

    PENDING_PAYMENT(1, "待支付"),
    PENDING_SHIPMENT(2, "待发货"),
    DELIVERING(3, "配送中"),
    COMPLETED(4, "已完成"),
    CANCELLED(5, "已取消"),
    AFTER_SALE(6, "售后中");

    private final Integer code;
    private final String desc;

    public static OrderStatus getByCode(Integer code) {
        if (code == null) {
            return null;
        }
        for (OrderStatus status : values()) {
            if (status.getCode().equals(code)) {
                return status;
            }
        }
        return null;
    }

}
