package com.fresh.settlement.service.impl;

import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.fresh.common.exception.BusinessException;
import com.fresh.common.result.ResultCode;
import com.fresh.settlement.dto.LossRecordAddDTO;
import com.fresh.settlement.dto.LossRecordQueryDTO;
import com.fresh.settlement.entity.LossRecord;
import com.fresh.settlement.enums.LossType;
import com.fresh.settlement.enums.ResponsibleParty;
import com.fresh.settlement.mapper.LossRecordMapper;
import com.fresh.settlement.service.LossRecordService;
import com.fresh.settlement.vo.LossRecordVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
public class LossRecordServiceImpl extends ServiceImpl<LossRecordMapper, LossRecord> implements LossRecordService {

    private static final String LOSS_NO_PREFIX = "LR";

    @Override
    @Transactional(rollbackFor = Exception.class)
    public String addLossRecord(LossRecordAddDTO dto) {
        String lossNo = generateLossNo();

        LossRecord lossRecord = new LossRecord();
        BeanUtils.copyProperties(dto, lossRecord);
        lossRecord.setLossNo(lossNo);
        this.save(lossRecord);

        return lossNo;
    }

    @Override
    public Page<LossRecordVO> lossRecordPage(LossRecordQueryDTO dto) {
        LambdaQueryWrapper<LossRecord> wrapper = new LambdaQueryWrapper<>();
        if (StrUtil.isNotBlank(dto.getLossNo())) {
            wrapper.like(LossRecord::getLossNo, dto.getLossNo());
        }
        if (dto.getType() != null) {
            wrapper.eq(LossRecord::getType, dto.getType());
        }
        if (dto.getResponsibleParty() != null) {
            wrapper.eq(LossRecord::getResponsibleParty, dto.getResponsibleParty());
        }
        if (dto.getSettlementId() != null) {
            wrapper.eq(LossRecord::getSettlementId, dto.getSettlementId());
        }
        if (dto.getStartTime() != null) {
            wrapper.ge(LossRecord::getCreateTime, dto.getStartTime());
        }
        if (dto.getEndTime() != null) {
            wrapper.le(LossRecord::getCreateTime, dto.getEndTime());
        }
        wrapper.orderByDesc(LossRecord::getCreateTime);

        Page<LossRecord> page = this.page(new Page<>(dto.getPageNum(), dto.getPageSize()), wrapper);
        Page<LossRecordVO> voPage = new Page<>(page.getCurrent(), page.getSize(), page.getTotal());

        List<LossRecordVO> voList = page.getRecords().stream().map(record -> {
            LossRecordVO vo = new LossRecordVO();
            BeanUtils.copyProperties(record, vo);
            LossType lossType = LossType.getByCode(record.getType());
            if (lossType != null) {
                vo.setTypeDesc(lossType.getDesc());
            }
            ResponsibleParty party = ResponsibleParty.getByCode(record.getResponsibleParty());
            if (party != null) {
                vo.setResponsiblePartyDesc(party.getDesc());
            }
            return vo;
        }).collect(Collectors.toList());

        voPage.setRecords(voList);
        return voPage;
    }

    @Override
    public LossRecordVO lossRecordDetail(Long id) {
        LossRecord record = this.getById(id);
        if (record == null) {
            throw new BusinessException(ResultCode.PARAM_ERROR.getCode(), "损耗记录不存在");
        }

        LossRecordVO vo = new LossRecordVO();
        BeanUtils.copyProperties(record, vo);

        LossType lossType = LossType.getByCode(record.getType());
        if (lossType != null) {
            vo.setTypeDesc(lossType.getDesc());
        }

        ResponsibleParty party = ResponsibleParty.getByCode(record.getResponsibleParty());
        if (party != null) {
            vo.setResponsiblePartyDesc(party.getDesc());
        }

        return vo;
    }

    private String generateLossNo() {
        return LOSS_NO_PREFIX + IdUtil.getSnowflakeNextIdStr();
    }
}
