package com.fresh.aftersale.strategy;

import com.fresh.aftersale.dto.AftersaleApplyDTO;
import com.fresh.aftersale.entity.AftersaleOrder;

public interface AftersaleStrategy {

    Integer getType();

    void validate(AftersaleApplyDTO dto);

    void processAftersale(AftersaleOrder order);

    void processRefund(AftersaleOrder order);

    void restoreStock(AftersaleOrder order);

}
