package com.fresh.settlement.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.fresh.settlement.dto.SettlementRuleDTO;
import com.fresh.settlement.entity.SettlementRule;
import com.fresh.settlement.vo.SettlementRuleVO;

import java.util.List;

public interface SettlementRuleService extends IService<SettlementRule> {

    Long addRule(SettlementRuleDTO dto);

    Boolean updateRule(Long id, SettlementRuleDTO dto);

    Page<SettlementRuleVO> rulePage(Integer ruleType, Integer pageNum, Integer pageSize);

    SettlementRuleVO ruleDetail(Long id);

    List<SettlementRuleVO> getEffectiveRules(Integer ruleType);

    Boolean enableRule(Long id);

    Boolean disableRule(Long id);

}
