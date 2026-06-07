package com.fresh.aftersale.strategy;

import com.fresh.aftersale.dto.AftersaleApplyDTO;
import com.fresh.aftersale.entity.AftersaleItem;
import com.fresh.aftersale.entity.AftersaleOrder;
import com.fresh.aftersale.enums.AftersaleTypeEnum;
import com.fresh.common.exception.BusinessException;
import com.fresh.common.result.ResultCode;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

@Component
public class RejectRefundStrategy extends AbstractAftersaleStrategy {

    @Override
    public Integer getType() {
        return AftersaleTypeEnum.REJECT_REFUND.getCode();
    }

    @Override
    public void validate(AftersaleApplyDTO dto) {
        if (dto.getItems() == null || dto.getItems().isEmpty()) {
            throw new BusinessException(ResultCode.PARAM_ERROR.getCode(), "拒收退款必须包含商品明细");
        }
    }

    @Override
    public void restoreStock(AftersaleOrder order) {
        List<AftersaleItem> items = aftersaleItemService.getByAftersaleId(order.getId());
        for (AftersaleItem item : items) {
            productFeignClient.restoreStock(
                    item.getSkuId(),
                    item.getRefundQty(),
                    null,
                    "拒收售后回补：" + order.getAftersaleNo()
            );
        }
    }
}
