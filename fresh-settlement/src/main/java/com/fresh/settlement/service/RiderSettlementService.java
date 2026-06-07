package com.fresh.settlement.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.fresh.settlement.dto.RiderSettlementQueryDTO;
import com.fresh.settlement.entity.RiderSettlement;
import com.fresh.settlement.vo.RiderSettlementVO;

import java.time.LocalDateTime;

public interface RiderSettlementService extends IService<RiderSettlement> {

    String generateSettlement(Long riderId, LocalDateTime periodStart, LocalDateTime periodEnd);

    Page<RiderSettlementVO> settlementPage(RiderSettlementQueryDTO dto);

    RiderSettlementVO settlementDetail(Long id);

    Boolean confirmSettlement(Long id);

    Boolean paySettlement(Long id);

}
