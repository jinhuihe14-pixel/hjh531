package com.fresh.settlement.service.impl;

import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.fresh.common.exception.BusinessException;
import com.fresh.common.result.ResultCode;
import com.fresh.settlement.dto.SupplierSettlementGenerateDTO;
import com.fresh.settlement.dto.SupplierSettlementQueryDTO;
import com.fresh.settlement.entity.Supplier;
import com.fresh.settlement.entity.SupplierSettlement;
import com.fresh.settlement.entity.SupplierSettlementDetail;
import com.fresh.settlement.enums.SupplierSettlementStatus;
import com.fresh.settlement.mapper.SupplierSettlementMapper;
import com.fresh.settlement.service.SupplierService;
import com.fresh.settlement.service.SupplierSettlementDetailService;
import com.fresh.settlement.service.SupplierSettlementService;
import com.fresh.settlement.vo.SupplierSettlementDetailVO;
import com.fresh.settlement.vo.SupplierSettlementVO;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
public class SupplierSettlementServiceImpl extends ServiceImpl<SupplierSettlementMapper, SupplierSettlement> implements SupplierSettlementService {

    @Resource
    private SupplierService supplierService;

    @Resource
    private SupplierSettlementDetailService supplierSettlementDetailService;

    private static final String SETTLEMENT_NO_PREFIX = "SS";

    @Override
    @Transactional(rollbackFor = Exception.class)
    public String generateSettlement(SupplierSettlementGenerateDTO dto) {
        Supplier supplier = supplierService.getById(dto.getSupplierId());
        if (supplier == null) {
            throw new BusinessException(ResultCode.PARAM_ERROR.getCode(), "供应商不存在");
        }

        String settlementNo = generateSettlementNo();

        SupplierSettlement settlement = new SupplierSettlement();
        settlement.setSettlementNo(settlementNo);
        settlement.setSupplierId(dto.getSupplierId());
        settlement.setPeriodStart(dto.getPeriodStart());
        settlement.setPeriodEnd(dto.getPeriodEnd());
        settlement.setTotalAmount(BigDecimal.ZERO);
        settlement.setDeductionAmount(BigDecimal.ZERO);
        settlement.setActualAmount(BigDecimal.ZERO);
        settlement.setStatus(SupplierSettlementStatus.PENDING_RECONCILIATION.getCode());
        this.save(settlement);

        return settlementNo;
    }

    @Override
    public Page<SupplierSettlementVO> settlementPage(SupplierSettlementQueryDTO dto) {
        LambdaQueryWrapper<SupplierSettlement> wrapper = new LambdaQueryWrapper<>();
        if (StrUtil.isNotBlank(dto.getSettlementNo())) {
            wrapper.like(SupplierSettlement::getSettlementNo, dto.getSettlementNo());
        }
        if (dto.getSupplierId() != null) {
            wrapper.eq(SupplierSettlement::getSupplierId, dto.getSupplierId());
        }
        if (dto.getStatus() != null) {
            wrapper.eq(SupplierSettlement::getStatus, dto.getStatus());
        }
        if (dto.getStartTime() != null) {
            wrapper.ge(SupplierSettlement::getCreateTime, dto.getStartTime());
        }
        if (dto.getEndTime() != null) {
            wrapper.le(SupplierSettlement::getCreateTime, dto.getEndTime());
        }
        wrapper.orderByDesc(SupplierSettlement::getCreateTime);

        Page<SupplierSettlement> page = this.page(new Page<>(dto.getPageNum(), dto.getPageSize()), wrapper);
        Page<SupplierSettlementVO> voPage = new Page<>(page.getCurrent(), page.getSize(), page.getTotal());

        List<SupplierSettlementVO> voList = page.getRecords().stream().map(settlement -> {
            SupplierSettlementVO vo = new SupplierSettlementVO();
            BeanUtils.copyProperties(settlement, vo);
            SupplierSettlementStatus status = SupplierSettlementStatus.getByCode(settlement.getStatus());
            if (status != null) {
                vo.setStatusDesc(status.getDesc());
            }
            Supplier supplier = supplierService.getById(settlement.getSupplierId());
            if (supplier != null) {
                vo.setSupplierName(supplier.getName());
            }
            return vo;
        }).collect(Collectors.toList());

        voPage.setRecords(voList);
        return voPage;
    }

    @Override
    public SupplierSettlementVO settlementDetail(Long id) {
        SupplierSettlement settlement = this.getById(id);
        if (settlement == null) {
            throw new BusinessException(ResultCode.PARAM_ERROR.getCode(), "结算单不存在");
        }

        SupplierSettlementVO vo = new SupplierSettlementVO();
        BeanUtils.copyProperties(settlement, vo);

        SupplierSettlementStatus status = SupplierSettlementStatus.getByCode(settlement.getStatus());
        if (status != null) {
            vo.setStatusDesc(status.getDesc());
        }

        Supplier supplier = supplierService.getById(settlement.getSupplierId());
        if (supplier != null) {
            vo.setSupplierName(supplier.getName());
        }

        List<SupplierSettlementDetail> details = supplierSettlementDetailService.list(
                new LambdaQueryWrapper<SupplierSettlementDetail>()
                        .eq(SupplierSettlementDetail::getSettlementId, id)
        );

        List<SupplierSettlementDetailVO> detailVOList = details.stream().map(detail -> {
            SupplierSettlementDetailVO detailVO = new SupplierSettlementDetailVO();
            BeanUtils.copyProperties(detail, detailVO);
            return detailVO;
        }).collect(Collectors.toList());

        vo.setDetails(detailVOList);
        return vo;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean confirmSettlement(Long id) {
        SupplierSettlement settlement = this.getById(id);
        if (settlement == null) {
            throw new BusinessException(ResultCode.PARAM_ERROR.getCode(), "结算单不存在");
        }
        if (!SupplierSettlementStatus.PENDING_RECONCILIATION.getCode().equals(settlement.getStatus())) {
            throw new BusinessException(ResultCode.PARAM_ERROR.getCode(), "结算单状态不正确");
        }

        settlement.setStatus(SupplierSettlementStatus.CONFIRMED.getCode());
        return this.updateById(settlement);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean paySettlement(Long id) {
        SupplierSettlement settlement = this.getById(id);
        if (settlement == null) {
            throw new BusinessException(ResultCode.PARAM_ERROR.getCode(), "结算单不存在");
        }
        if (!SupplierSettlementStatus.CONFIRMED.getCode().equals(settlement.getStatus())) {
            throw new BusinessException(ResultCode.PARAM_ERROR.getCode(), "结算单状态不正确");
        }

        settlement.setStatus(SupplierSettlementStatus.PAID.getCode());
        settlement.setSettlementTime(LocalDateTime.now());
        return this.updateById(settlement);
    }

    private String generateSettlementNo() {
        return SETTLEMENT_NO_PREFIX + IdUtil.getSnowflakeNextIdStr();
    }
}
