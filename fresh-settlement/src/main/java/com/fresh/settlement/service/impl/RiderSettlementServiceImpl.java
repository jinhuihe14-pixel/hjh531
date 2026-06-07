package com.fresh.settlement.service.impl;

import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.fresh.common.exception.BusinessException;
import com.fresh.common.result.ResultCode;
import com.fresh.settlement.dto.RiderSettlementQueryDTO;
import com.fresh.settlement.entity.RiderSettlement;
import com.fresh.settlement.enums.RiderSettlementStatus;
import com.fresh.settlement.mapper.RiderSettlementMapper;
import com.fresh.settlement.service.RiderSettlementService;
import com.fresh.settlement.vo.RiderSettlementVO;
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
public class RiderSettlementServiceImpl extends ServiceImpl<RiderSettlementMapper, RiderSettlement> implements RiderSettlementService {

    private static final String SETTLEMENT_NO_PREFIX = "RS";

    @Override
    @Transactional(rollbackFor = Exception.class)
    public String generateSettlement(Long riderId, LocalDateTime periodStart, LocalDateTime periodEnd) {
        String settlementNo = generateSettlementNo();

        RiderSettlement settlement = new RiderSettlement();
        settlement.setSettlementNo(settlementNo);
        settlement.setRiderId(riderId);
        settlement.setPeriodStart(periodStart);
        settlement.setPeriodEnd(periodEnd);
        settlement.setOrderCount(0);
        settlement.setTotalDistance(BigDecimal.ZERO);
        settlement.setBaseAmount(BigDecimal.ZERO);
        settlement.setDistanceAmount(BigDecimal.ZERO);
        settlement.setTimeSubsidy(BigDecimal.ZERO);
        settlement.setPenaltyAmount(BigDecimal.ZERO);
        settlement.setTotalAmount(BigDecimal.ZERO);
        settlement.setStatus(RiderSettlementStatus.PENDING.getCode());
        this.save(settlement);

        return settlementNo;
    }

    @Override
    public Page<RiderSettlementVO> settlementPage(RiderSettlementQueryDTO dto) {
        LambdaQueryWrapper<RiderSettlement> wrapper = new LambdaQueryWrapper<>();
        if (StrUtil.isNotBlank(dto.getSettlementNo())) {
            wrapper.like(RiderSettlement::getSettlementNo, dto.getSettlementNo());
        }
        if (dto.getRiderId() != null) {
            wrapper.eq(RiderSettlement::getRiderId, dto.getRiderId());
        }
        if (dto.getStatus() != null) {
            wrapper.eq(RiderSettlement::getStatus, dto.getStatus());
        }
        if (dto.getStartTime() != null) {
            wrapper.ge(RiderSettlement::getCreateTime, dto.getStartTime());
        }
        if (dto.getEndTime() != null) {
            wrapper.le(RiderSettlement::getCreateTime, dto.getEndTime());
        }
        wrapper.orderByDesc(RiderSettlement::getCreateTime);

        Page<RiderSettlement> page = this.page(new Page<>(dto.getPageNum(), dto.getPageSize()), wrapper);
        Page<RiderSettlementVO> voPage = new Page<>(page.getCurrent(), page.getSize(), page.getTotal());

        List<RiderSettlementVO> voList = page.getRecords().stream().map(settlement -> {
            RiderSettlementVO vo = new RiderSettlementVO();
            BeanUtils.copyProperties(settlement, vo);
            RiderSettlementStatus status = RiderSettlementStatus.getByCode(settlement.getStatus());
            if (status != null) {
                vo.setStatusDesc(status.getDesc());
            }
            return vo;
        }).collect(Collectors.toList());

        voPage.setRecords(voList);
        return voPage;
    }

    @Override
    public RiderSettlementVO settlementDetail(Long id) {
        RiderSettlement settlement = this.getById(id);
        if (settlement == null) {
            throw new BusinessException(ResultCode.PARAM_ERROR.getCode(), "结算单不存在");
        }

        RiderSettlementVO vo = new RiderSettlementVO();
        BeanUtils.copyProperties(settlement, vo);

        RiderSettlementStatus status = RiderSettlementStatus.getByCode(settlement.getStatus());
        if (status != null) {
            vo.setStatusDesc(status.getDesc());
        }

        return vo;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean confirmSettlement(Long id) {
        RiderSettlement settlement = this.getById(id);
        if (settlement == null) {
            throw new BusinessException(ResultCode.PARAM_ERROR.getCode(), "结算单不存在");
        }
        if (!RiderSettlementStatus.PENDING.getCode().equals(settlement.getStatus())) {
            throw new BusinessException(ResultCode.PARAM_ERROR.getCode(), "结算单状态不正确");
        }

        settlement.setStatus(RiderSettlementStatus.CONFIRMED.getCode());
        return this.updateById(settlement);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean paySettlement(Long id) {
        RiderSettlement settlement = this.getById(id);
        if (settlement == null) {
            throw new BusinessException(ResultCode.PARAM_ERROR.getCode(), "结算单不存在");
        }
        if (!RiderSettlementStatus.CONFIRMED.getCode().equals(settlement.getStatus())) {
            throw new BusinessException(ResultCode.PARAM_ERROR.getCode(), "结算单状态不正确");
        }

        settlement.setStatus(RiderSettlementStatus.PAID.getCode());
        return this.updateById(settlement);
    }

    private String generateSettlementNo() {
        return SETTLEMENT_NO_PREFIX + IdUtil.getSnowflakeNextIdStr();
    }
}
