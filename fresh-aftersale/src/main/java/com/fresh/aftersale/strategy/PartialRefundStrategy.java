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
public class PartialRefundStrategy extends AbstractAftersaleStrategy {

    @Override
    public Integer getType() {
        return AftersaleTypeEnum.PARTIAL_REFUND.getCode();
    }

    @Override
    public void validate(AftersaleApplyDTO dto) {
        if (dto.getApplyAmount() == null || dto.getApplyAmount().compareTo(java.math.BigDecimal.ZERO) <= 0) {
            throw new BusinessException(ResultCode.PARAM_ERROR.getCode(), "部分退款必须填写退款金额");
        }
    }

    @Override
    public void restoreStock(AftersaleOrder order) {
        List<AftersaleItem> items = aftersaleItemService.getByAftersaleId(order.getId());
        for (AftersaleItem item : items) {
            if (item.getRefundQty() != null && item.getRefundQty() > 0) {
                productFeignClient.restoreStock(
                        item.getSkuId(),
                        item.getRefundQty(),
                        null,
                        "部分退款回补：" + order.getAftersaleNo()
                );
            }
        }
    }
}
