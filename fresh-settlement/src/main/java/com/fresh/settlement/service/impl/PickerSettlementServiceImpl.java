package com.fresh.settlement.service.impl;

import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.fresh.common.exception.BusinessException;
import com.fresh.common.result.ResultCode;
import com.fresh.settlement.dto.PickerSettlementQueryDTO;
import com.fresh.settlement.entity.PickerSettlement;
import com.fresh.settlement.enums.PickerSettlementStatus;
import com.fresh.settlement.mapper.PickerSettlementMapper;
import com.fresh.settlement.service.PickerSettlementService;
import com.fresh.settlement.vo.PickerSettlementVO;
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
public class PickerSettlementServiceImpl extends ServiceImpl<PickerSettlementMapper, PickerSettlement> implements PickerSettlementService {

    private static final String SETTLEMENT_NO_PREFIX = "PS";

    @Override
    @Transactional(rollbackFor = Exception.class)
    public String generateSettlement(Long pickerId, LocalDateTime periodStart, LocalDateTime periodEnd) {
        String settlementNo = generateSettlementNo();

        PickerSettlement settlement = new PickerSettlement();
        settlement.setSettlementNo(settlementNo);
        settlement.setPickerId(pickerId);
        settlement.setPeriodStart(periodStart);
        settlement.setPeriodEnd(periodEnd);
        settlement.setTotalPieces(0);
        settlement.setBaseAmount(BigDecimal.ZERO);
        settlement.setSubsidy(BigDecimal.ZERO);
        settlement.setTotalAmount(BigDecimal.ZERO);
        settlement.setStatus(PickerSettlementStatus.PENDING.getCode());
        this.save(settlement);

        return settlementNo;
    }

    @Override
    public Page<PickerSettlementVO> settlementPage(PickerSettlementQueryDTO dto) {
        LambdaQueryWrapper<PickerSettlement> wrapper = new LambdaQueryWrapper<>();
        if (StrUtil.isNotBlank(dto.getSettlementNo())) {
            wrapper.like(PickerSettlement::getSettlementNo, dto.getSettlementNo());
        }
        if (dto.getPickerId() != null) {
            wrapper.eq(PickerSettlement::getPickerId, dto.getPickerId());
        }
        if (dto.getStatus() != null) {
            wrapper.eq(PickerSettlement::getStatus, dto.getStatus());
        }
        if (dto.getStartTime() != null) {
            wrapper.ge(PickerSettlement::getCreateTime, dto.getStartTime());
        }
        if (dto.getEndTime() != null) {
            wrapper.le(PickerSettlement::getCreateTime, dto.getEndTime());
        }
        wrapper.orderByDesc(PickerSettlement::getCreateTime);

        Page<PickerSettlement> page = this.page(new Page<>(dto.getPageNum(), dto.getPageSize()), wrapper);
        Page<PickerSettlementVO> voPage = new Page<>(page.getCurrent(), page.getSize(), page.getTotal());

        List<PickerSettlementVO> voList = page.getRecords().stream().map(settlement -> {
            PickerSettlementVO vo = new PickerSettlementVO();
            BeanUtils.copyProperties(settlement, vo);
            PickerSettlementStatus status = PickerSettlementStatus.getByCode(settlement.getStatus());
            if (status != null) {
                vo.setStatusDesc(status.getDesc());
            }
            return vo;
        }).collect(Collectors.toList());

        voPage.setRecords(voList);
        return voPage;
    }

    @Override
    public PickerSettlementVO settlementDetail(Long id) {
        PickerSettlement settlement = this.getById(id);
        if (settlement == null) {
            throw new BusinessException(ResultCode.PARAM_ERROR.getCode(), "结算单不存在");
        }

        PickerSettlementVO vo = new PickerSettlementVO();
        BeanUtils.copyProperties(settlement, vo);

        PickerSettlementStatus status = PickerSettlementStatus.getByCode(settlement.getStatus());
        if (status != null) {
            vo.setStatusDesc(status.getDesc());
        }

        return vo;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean confirmSettlement(Long id) {
        PickerSettlement settlement = this.getById(id);
        if (settlement == null) {
            throw new BusinessException(ResultCode.PARAM_ERROR.getCode(), "结算单不存在");
        }
        if (!PickerSettlementStatus.PENDING.getCode().equals(settlement.getStatus())) {
            throw new BusinessException(ResultCode.PARAM_ERROR.getCode(), "结算单状态不正确");
        }

        settlement.setStatus(PickerSettlementStatus.CONFIRMED.getCode());
        return this.updateById(settlement);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean paySettlement(Long id) {
        PickerSettlement settlement = this.getById(id);
        if (settlement == null) {
            throw new BusinessException(ResultCode.PARAM_ERROR.getCode(), "结算单不存在");
        }
        if (!PickerSettlementStatus.CONFIRMED.getCode().equals(settlement.getStatus())) {
            throw new BusinessException(ResultCode.PARAM_ERROR.getCode(), "结算单状态不正确");
        }

        settlement.setStatus(PickerSettlementStatus.PAID.getCode());
        return this.updateById(settlement);
    }

    private String generateSettlementNo() {
        return SETTLEMENT_NO_PREFIX + IdUtil.getSnowflakeNextIdStr();
    }
}
