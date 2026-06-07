package com.fresh.settlement.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.fresh.common.exception.BusinessException;
import com.fresh.common.result.ResultCode;
import com.fresh.settlement.dto.SettlementRuleDTO;
import com.fresh.settlement.entity.SettlementRule;
import com.fresh.settlement.enums.SettlementRuleType;
import com.fresh.settlement.mapper.SettlementRuleMapper;
import com.fresh.settlement.service.SettlementRuleService;
import com.fresh.settlement.vo.SettlementRuleVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
public class SettlementRuleServiceImpl extends ServiceImpl<SettlementRuleMapper, SettlementRule> implements SettlementRuleService {

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long addRule(SettlementRuleDTO dto) {
        LambdaQueryWrapper<SettlementRule> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SettlementRule::getRuleType, dto.getRuleType());
        wrapper.orderByDesc(SettlementRule::getVersion);
        wrapper.last("LIMIT 1");
        SettlementRule lastRule = this.getOne(wrapper);

        int version = 1;
        if (lastRule != null) {
            version = lastRule.getVersion() + 1;
        }

        SettlementRule rule = new SettlementRule();
        BeanUtils.copyProperties(dto, rule);
        rule.setVersion(version);
        rule.setStatus(1);
        this.save(rule);

        return rule.getId();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean updateRule(Long id, SettlementRuleDTO dto) {
        SettlementRule rule = this.getById(id);
        if (rule == null) {
            throw new BusinessException(ResultCode.PARAM_ERROR.getCode(), "规则不存在");
        }

        BeanUtils.copyProperties(dto, rule);
        return this.updateById(rule);
    }

    @Override
    public Page<SettlementRuleVO> rulePage(Integer ruleType, Integer pageNum, Integer pageSize) {
        LambdaQueryWrapper<SettlementRule> wrapper = new LambdaQueryWrapper<>();
        if (ruleType != null) {
            wrapper.eq(SettlementRule::getRuleType, ruleType);
        }
        wrapper.orderByDesc(SettlementRule::getCreateTime);

        Page<SettlementRule> page = this.page(new Page<>(pageNum, pageSize), wrapper);
        Page<SettlementRuleVO> voPage = new Page<>(page.getCurrent(), page.getSize(), page.getTotal());

        List<SettlementRuleVO> voList = page.getRecords().stream().map(rule -> {
            SettlementRuleVO vo = new SettlementRuleVO();
            BeanUtils.copyProperties(rule, vo);
            SettlementRuleType type = SettlementRuleType.getByCode(rule.getRuleType());
            if (type != null) {
                vo.setRuleTypeDesc(type.getDesc());
            }
            return vo;
        }).collect(Collectors.toList());

        voPage.setRecords(voList);
        return voPage;
    }

    @Override
    public SettlementRuleVO ruleDetail(Long id) {
        SettlementRule rule = this.getById(id);
        if (rule == null) {
            throw new BusinessException(ResultCode.PARAM_ERROR.getCode(), "规则不存在");
        }

        SettlementRuleVO vo = new SettlementRuleVO();
        BeanUtils.copyProperties(rule, vo);

        SettlementRuleType type = SettlementRuleType.getByCode(rule.getRuleType());
        if (type != null) {
            vo.setRuleTypeDesc(type.getDesc());
        }

        return vo;
    }

    @Override
    public List<SettlementRuleVO> getEffectiveRules(Integer ruleType) {
        LocalDateTime now = LocalDateTime.now();
        LambdaQueryWrapper<SettlementRule> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SettlementRule::getRuleType, ruleType);
        wrapper.eq(SettlementRule::getStatus, 1);
        wrapper.le(SettlementRule::getEffectiveTime, now);
        wrapper.and(w -> w.isNull(SettlementRule::getExpireTime).or().gt(SettlementRule::getExpireTime, now));
        wrapper.orderByDesc(SettlementRule::getVersion);

        List<SettlementRule> rules = this.list(wrapper);
        return rules.stream().map(rule -> {
            SettlementRuleVO vo = new SettlementRuleVO();
            BeanUtils.copyProperties(rule, vo);
            SettlementRuleType type = SettlementRuleType.getByCode(rule.getRuleType());
            if (type != null) {
                vo.setRuleTypeDesc(type.getDesc());
            }
            return vo;
        }).collect(Collectors.toList());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean enableRule(Long id) {
        SettlementRule rule = this.getById(id);
        if (rule == null) {
            throw new BusinessException(ResultCode.PARAM_ERROR.getCode(), "规则不存在");
        }
        rule.setStatus(1);
        return this.updateById(rule);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean disableRule(Long id) {
        SettlementRule rule = this.getById(id);
        if (rule == null) {
            throw new BusinessException(ResultCode.PARAM_ERROR.getCode(), "规则不存在");
        }
        rule.setStatus(0);
        return this.updateById(rule);
    }
}
