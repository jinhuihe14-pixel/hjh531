package com.fresh.aftersale.controller;

import com.fresh.aftersale.dto.AftersaleApprovalDTO;
import com.fresh.aftersale.dto.AftersaleHandleDTO;
import com.fresh.aftersale.dto.AftersaleQueryDTO;
import com.fresh.aftersale.service.AftersaleOrderService;
import com.fresh.aftersale.vo.AftersaleDetailVO;
import com.fresh.aftersale.vo.AftersaleOrderVO;
import com.fresh.common.page.PageResult;
import com.fresh.common.result.R;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.*;

@Tag(name = "客服端-售后接口")
@RestController
@RequestMapping("/aftersale/service")
public class AftersaleServiceController {

    @Resource
    private AftersaleOrderService aftersaleOrderService;

    @Operation(summary = "售后列表")
    @GetMapping("/list")
    public R<PageResult<AftersaleOrderVO>> getAftersaleList(AftersaleQueryDTO dto) {
        PageResult<AftersaleOrderVO> pageResult = aftersaleOrderService.getAdminAftersaleList(dto);
        return R.success(pageResult);
    }

    @Operation(summary = "售后详情")
    @GetMapping("/{id}")
    public R<AftersaleDetailVO> getAftersaleDetail(@PathVariable("id") Long id) {
        AftersaleDetailVO detailVO = aftersaleOrderService.getAftersaleDetail(id);
        return R.success(detailVO);
    }

    @Operation(summary = "审核售后")
    @PostMapping("/review")
    public R<Boolean> reviewAftersale(@RequestBody AftersaleHandleDTO dto) {
        Boolean result = aftersaleOrderService.reviewAftersale(dto);
        return R.success(result);
    }

    @Operation(summary = "处理售后")
    @PostMapping("/handle")
    public R<Boolean> handleAftersale(@RequestBody AftersaleHandleDTO dto) {
        Boolean result = aftersaleOrderService.handleAftersale(dto);
        return R.success(result);
    }

    @Operation(summary = "确认退款")
    @PostMapping("/confirmRefund/{id}")
    public R<Boolean> confirmRefund(@PathVariable("id") Long id) {
        Boolean result = aftersaleOrderService.confirmRefund(id);
        return R.success(result);
    }

    @Operation(summary = "审批售后")
    @PostMapping("/approval")
    public R<Boolean> approvalAftersale(@RequestBody AftersaleApprovalDTO dto) {
        Boolean result = aftersaleOrderService.approvalAftersale(dto);
        return R.success(result);
    }
}
