package com.fresh.warehouse.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.fresh.common.page.PageQuery;
import com.fresh.warehouse.dto.StockTransferCreateDTO;
import com.fresh.warehouse.dto.StockTransferQueryDTO;
import com.fresh.warehouse.entity.StockTransfer;
import com.fresh.warehouse.vo.StockTransferDetailVO;

public interface StockTransferService extends IService<StockTransfer> {

    IPage<StockTransfer> page(PageQuery pageQuery, StockTransferQueryDTO queryDTO);

    StockTransferDetailVO getDetailById(Long id);

    void createTransfer(StockTransferCreateDTO dto);

    void approve(Long id, Boolean pass, String remark);

    void outbound(Long id);

    void inbound(Long id);
}
