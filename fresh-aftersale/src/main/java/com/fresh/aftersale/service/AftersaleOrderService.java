package com.fresh.aftersale.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.fresh.aftersale.dto.AftersaleApplyDTO;
import com.fresh.aftersale.dto.AftersaleApprovalDTO;
import com.fresh.aftersale.dto.AftersaleHandleDTO;
import com.fresh.aftersale.dto.AftersaleQueryDTO;
import com.fresh.aftersale.entity.AftersaleOrder;
import com.fresh.aftersale.vo.AftersaleDetailVO;
import com.fresh.aftersale.vo.AftersaleOrderVO;
import com.fresh.aftersale.vo.AftersaleStatisticsVO;
import com.fresh.common.page.PageResult;

public interface AftersaleOrderService extends IService<AftersaleOrder> {

    String applyAftersale(AftersaleApplyDTO dto);

    PageResult<AftersaleOrderVO> getUserAftersaleList(AftersaleQueryDTO dto);

    AftersaleDetailVO getAftersaleDetail(Long id);

    Boolean cancelAftersale(Long id);

    PageResult<AftersaleOrderVO> getAdminAftersaleList(AftersaleQueryDTO dto);

    Boolean reviewAftersale(AftersaleHandleDTO dto);

    Boolean handleAftersale(AftersaleHandleDTO dto);

    Boolean confirmRefund(Long id);

    AftersaleStatisticsVO getStatistics();

    Boolean approvalAftersale(AftersaleApprovalDTO dto);

}
