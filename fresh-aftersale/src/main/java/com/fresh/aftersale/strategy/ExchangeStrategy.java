package com.fresh.aftersale.strategy;

import com.fresh.aftersale.dto.AftersaleApplyDTO;
import com.fresh.aftersale.entity.AftersaleOrder;
import com.fresh.aftersale.enums.AftersaleTypeEnum;
import org.springframework.stereotype.Component;

@Component
public class ExchangeStrategy extends AbstractAftersaleStrategy {

    @Override
    public Integer getType() {
        return AftersaleTypeEnum.EXCHANGE.getCode();
    }

    @Override
    public void validate(AftersaleApplyDTO dto) {
    }

    @Override
    public void processAftersale(AftersaleOrder order) {
    }
}
