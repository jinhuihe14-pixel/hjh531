package com.fresh.settlement.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum SettlementRuleType {

    DELIVERY_SUBSIDY(1, "配送补贴标准"),
    PICKER_PIECE_RATE(2, "分拣计件单价"),
    LOSS_ALLOCATION(3, "损耗分摊规则");

    private final Integer code;
    private final String desc;

    public static SettlementRuleType getByCode(Integer code) {
        if (code == null) {
            return null;
        }
        for (SettlementRuleType type : values()) {
            if (type.getCode().equals(code)) {
                return type;
            }
        }
        return null;
    }

}
