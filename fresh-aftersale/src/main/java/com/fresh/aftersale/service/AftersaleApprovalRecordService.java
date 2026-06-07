package com.fresh.aftersale.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.fresh.aftersale.entity.AftersaleApprovalRecord;

import java.util.List;

public interface AftersaleApprovalRecordService extends IService<AftersaleApprovalRecord> {

    List<AftersaleApprovalRecord> getByAftersaleId(Long aftersaleId);

    void addRecord(Long aftersaleId, Integer approvalLevel, Integer approvalStatus, Long approverId, String approverName, String approvalRemark);

}
