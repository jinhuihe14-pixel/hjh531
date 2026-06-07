package com.fresh.warehouse.enums;

import lombok.Getter;

@Getter
public enum StockTransferStatus {

    PENDING(1, "待审批"),
    APPROVED(2, "已批准"),
    REJECTED(3, "已驳回"),
    TRANSFERRING(4, "调拨中"),
    COMPLETED(5, "已完成");

    private final Integer code;
    private final String desc;

    StockTransferStatus(Integer code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    public static StockTransferStatus getByCode(Integer code) {
        for (StockTransferStatus status : values()) {
            if (status.getCode().equals(code)) {
                return status;
            }
        }
        return null;
    }
}
