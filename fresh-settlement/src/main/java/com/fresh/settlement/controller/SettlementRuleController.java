package com.fresh.settlement.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.fresh.common.result.R;
import com.fresh.settlement.dto.SettlementRuleDTO;
import com.fresh.settlement.service.SettlementRuleService;
import com.fresh.settlement.vo.SettlementRuleVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "结算规则", description = "结算规则配置管理接口")
@RestController
@RequestMapping("/settlement/rule")
public class SettlementRuleController {

    @Resource
    private SettlementRuleService settlementRuleService;

    @Operation(summary = "新增规则")
    @PostMapping("/add")
    public R<Long> addRule(@Valid @RequestBody SettlementRuleDTO dto) {
        Long id = settlementRuleService.addRule(dto);
        return R.success(id);
    }

    @Operation(summary = "更新规则")
    @PostMapping("/update/{id}")
    public R<Boolean> updateRule(@PathVariable("id") Long id, @Valid @RequestBody SettlementRuleDTO dto) {
        Boolean result = settlementRuleService.updateRule(id, dto);
        return R.success(result);
    }

    @Operation(summary = "规则列表")
    @GetMapping("/list")
    public R<Page<SettlementRuleVO>> ruleList(
            @RequestParam(required = false) Integer ruleType,
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize) {
        Page<SettlementRuleVO> page = settlementRuleService.rulePage(ruleType, pageNum, pageSize);
        return R.success(page);
    }

    @Operation(summary = "规则详情")
    @GetMapping("/{id}")
    public R<SettlementRuleVO> ruleDetail(@PathVariable("id") Long id) {
        SettlementRuleVO detail = settlementRuleService.ruleDetail(id);
        return R.success(detail);
    }

    @Operation(summary = "获取生效规则")
    @GetMapping("/effective")
    public R<List<SettlementRuleVO>> getEffectiveRules(@RequestParam Integer ruleType) {
        List<SettlementRuleVO> rules = settlementRuleService.getEffectiveRules(ruleType);
        return R.success(rules);
    }

    @Operation(summary = "启用规则")
    @PostMapping("/enable/{id}")
    public R<Boolean> enableRule(@PathVariable("id") Long id) {
        Boolean result = settlementRuleService.enableRule(id);
        return R.success(result);
    }

    @Operation(summary = "禁用规则")
    @PostMapping("/disable/{id}")
    public R<Boolean> disableRule(@PathVariable("id") Long id) {
        Boolean result = settlementRuleService.disableRule(id);
        return R.success(result);
    }
}
