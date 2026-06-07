package com.fresh.aftersale.strategy;

import com.fresh.aftersale.dto.AftersaleApplyDTO;
import com.fresh.aftersale.entity.AftersaleItem;
import com.fresh.aftersale.entity.AftersaleOrder;
import com.fresh.aftersale.enums.AftersaleTypeEnum;
import com.fresh.common.exception.BusinessException;
import com.fresh.common.result.ResultCode;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class SpoiledRefundStrategy extends AbstractAftersaleStrategy {

    @Override
    public Integer getType() {
        return AftersaleTypeEnum.SPOILED_REFUND.getCode();
    }

    @Override
    public void validate(AftersaleApplyDTO dto) {
        if (dto.getReason() == null) {
            throw new BusinessException(ResultCode.PARAM_ERROR.getCode(), "商品变质退款必须填写变质原因");
        }
    }

    @Override
    public void restoreStock(AftersaleOrder order) {
    }
}
