package com.fresh.settlement.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.fresh.settlement.dto.SupplierSettlementGenerateDTO;
import com.fresh.settlement.dto.SupplierSettlementQueryDTO;
import com.fresh.settlement.entity.SupplierSettlement;
import com.fresh.settlement.vo.SupplierSettlementVO;

public interface SupplierSettlementService extends IService<SupplierSettlement> {

    String generateSettlement(SupplierSettlementGenerateDTO dto);

    Page<SupplierSettlementVO> settlementPage(SupplierSettlementQueryDTO dto);

    SupplierSettlementVO settlementDetail(Long id);

    Boolean confirmSettlement(Long id);

    Boolean paySettlement(Long id);

}
