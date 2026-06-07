package com.fresh.settlement.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.fresh.settlement.dto.LossRecordAddDTO;
import com.fresh.settlement.dto.LossRecordQueryDTO;
import com.fresh.settlement.entity.LossRecord;
import com.fresh.settlement.vo.LossRecordVO;

public interface LossRecordService extends IService<LossRecord> {

    String addLossRecord(LossRecordAddDTO dto);

    Page<LossRecordVO> lossRecordPage(LossRecordQueryDTO dto);

    LossRecordVO lossRecordDetail(Long id);

}
