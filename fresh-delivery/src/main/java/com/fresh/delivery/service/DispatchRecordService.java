package com.fresh.delivery.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.fresh.delivery.entity.DispatchRecord;

import java.math.BigDecimal;

public interface DispatchRecordService extends IService<DispatchRecord> {

    Boolean addDispatchRecord(Long deliveryId, String deliveryNo, Long riderId, String riderName,
                              Integer dispatchType, String dispatchReason, BigDecimal distance, Integer estimateTime);

}
