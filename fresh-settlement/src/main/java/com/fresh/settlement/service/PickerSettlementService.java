package com.fresh.settlement.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.fresh.settlement.dto.PickerSettlementQueryDTO;
import com.fresh.settlement.entity.PickerSettlement;
import com.fresh.settlement.vo.PickerSettlementVO;

import java.time.LocalDateTime;

public interface PickerSettlementService extends IService<PickerSettlement> {

    String generateSettlement(Long pickerId, LocalDateTime periodStart, LocalDateTime periodEnd);

    Page<PickerSettlementVO> settlementPage(PickerSettlementQueryDTO dto);

    PickerSettlementVO settlementDetail(Long id);

    Boolean confirmSettlement(Long id);

    Boolean paySettlement(Long id);

}
