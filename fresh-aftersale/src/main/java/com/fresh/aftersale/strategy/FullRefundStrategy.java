package com.fresh.aftersale.strategy;

import com.fresh.aftersale.dto.AftersaleApplyDTO;
import com.fresh.aftersale.entity.AftersaleItem;
import com.fresh.aftersale.entity.AftersaleOrder;
import com.fresh.aftersale.enums.AftersaleTypeEnum;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class FullRefundStrategy extends AbstractAftersaleStrategy {

    @Override
    public Integer getType() {
        return AftersaleTypeEnum.FULL_REFUND.getCode();
    }

    @Override
    public void validate(AftersaleApplyDTO dto) {
    }

    @Override
    public void restoreStock(AftersaleOrder order) {
        List<AftersaleItem> items = aftersaleItemService.getByAftersaleId(order.getId());
        for (AftersaleItem item : items) {
            productFeignClient.restoreStock(
                    item.getSkuId(),
                    item.getQty(),
                    null,
                    "整单退款回补：" + order.getAftersaleNo()
            );
        }
    }
}
