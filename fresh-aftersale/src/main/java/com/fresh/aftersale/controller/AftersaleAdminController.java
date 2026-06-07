package com.fresh.aftersale.controller;

import com.fresh.aftersale.service.AftersaleOrderService;
import com.fresh.aftersale.vo.AftersaleStatisticsVO;
import com.fresh.common.result.R;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

@Tag(name = "后台管理-售后接口")
@RestController
@RequestMapping("/aftersale/admin")
@RefreshScope
public class AftersaleAdminController {

    @Resource
    private AftersaleOrderService aftersaleOrderService;

    @Value("${aftersale.approval.small-amount:100}")
    private BigDecimal smallAmount;

    @Value("${aftersale.approval.medium-amount:500}")
    private BigDecimal mediumAmount;

    @Value("${aftersale.approval.large-amount:2000}")
    private BigDecimal largeAmount;

    @Operation(summary = "售后统计")
    @GetMapping("/statistics")
    public R<AftersaleStatisticsVO> getStatistics() {
        AftersaleStatisticsVO statisticsVO = aftersaleOrderService.getStatistics();
        return R.success(statisticsVO);
    }

    @Operation(summary = "获取审批流配置")
    @GetMapping("/approvalConfig")
    public R<Map<String, BigDecimal>> getApprovalConfig() {
        Map<String, BigDecimal> config = new HashMap<>();
        config.put("smallAmount", smallAmount);
        config.put("mediumAmount", mediumAmount);
        config.put("largeAmount", largeAmount);
        return R.success(config);
    }

    @Operation(summary = "更新审批流配置")
    @PostMapping("/approvalConfig")
    public R<Boolean> updateApprovalConfig(@RequestParam("smallAmount") BigDecimal smallAmount,
                                          @RequestParam("mediumAmount") BigDecimal mediumAmount,
                                          @RequestParam("largeAmount") BigDecimal largeAmount) {
        return R.success(true);
    }
}
