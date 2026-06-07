package com.fresh.delivery.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.fresh.delivery.entity.DispatchRecord;
import com.fresh.delivery.mapper.DispatchRecordMapper;
import com.fresh.delivery.service.DispatchRecordService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Slf4j
@Service
public class DispatchRecordServiceImpl extends ServiceImpl<DispatchRecordMapper, DispatchRecord> implements DispatchRecordService {

    @Override
    public Boolean addDispatchRecord(Long deliveryId, String deliveryNo, Long riderId, String riderName,
                                      Integer dispatchType, String dispatchReason, BigDecimal distance, Integer estimateTime) {
        DispatchRecord record = new DispatchRecord();
        record.setDeliveryId(deliveryId);
        record.setDeliveryNo(deliveryNo);
        record.setRiderId(riderId);
        record.setRiderName(riderName);
        record.setDispatchType(dispatchType);
        record.setDispatchReason(dispatchReason);
        record.setDistance(distance);
        record.setEstimateTime(estimateTime);
        record.setDispatchTime(LocalDateTime.now());
        return this.save(record);
    }
}
