package com.fresh.aftersale.strategy;

import com.fresh.aftersale.dto.AftersaleApplyDTO;
import com.fresh.aftersale.entity.AftersaleOrder;
import com.fresh.aftersale.enums.AftersaleTypeEnum;
import com.fresh.common.exception.BusinessException;
import com.fresh.common.result.ResultCode;
import org.springframework.stereotype.Component;

@Component
public class MissingResendStrategy extends AbstractAftersaleStrategy {

    @Override
    public Integer getType() {
        return AftersaleTypeEnum.MISSING_RESEND.getCode();
    }

    @Override
    public void validate(AftersaleApplyDTO dto) {
        if (dto.getItems() == null || dto.getItems().isEmpty()) {
            throw new BusinessException(ResultCode.PARAM_ERROR.getCode(), "少货补发必须填写缺少的商品明细");
        }
    }

    @Override
    public void processAftersale(AftersaleOrder order) {
    }
}
