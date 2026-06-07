package com.fresh.aftersale.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.fresh.aftersale.entity.AftersaleApprovalRecord;
import com.fresh.aftersale.mapper.AftersaleApprovalRecordMapper;
import com.fresh.aftersale.service.AftersaleApprovalRecordService;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class AftersaleApprovalRecordServiceImpl extends ServiceImpl<AftersaleApprovalRecordMapper, AftersaleApprovalRecord> implements AftersaleApprovalRecordService {

    @Override
    public List<AftersaleApprovalRecord> getByAftersaleId(Long aftersaleId) {
        LambdaQueryWrapper<AftersaleApprovalRecord> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(AftersaleApprovalRecord::getAftersaleId, aftersaleId);
        wrapper.orderByAsc(AftersaleApprovalRecord::getCreateTime);
        return list(wrapper);
    }

    @Override
    public void addRecord(Long aftersaleId, Integer approvalLevel, Integer approvalStatus, Long approverId, String approverName, String approvalRemark) {
        AftersaleApprovalRecord record = new AftersaleApprovalRecord();
        record.setAftersaleId(aftersaleId);
        record.setApprovalLevel(approvalLevel);
        record.setApprovalStatus(approvalStatus);
        record.setApproverId(approverId);
        record.setApproverName(approverName);
        record.setApprovalRemark(approvalRemark);
        record.setApprovalTime(LocalDateTime.now());
        save(record);
    }
}
