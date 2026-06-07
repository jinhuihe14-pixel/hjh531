package com.fresh.aftersale.controller;

import com.fresh.aftersale.dto.AftersaleApplyDTO;
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

@Tag(name = "用户端-售后接口")
@RestController
@RequestMapping("/aftersale/user")
public class AftersaleUserController {

    @Resource
    private AftersaleOrderService aftersaleOrderService;

    @Operation(summary = "申请售后")
    @PostMapping("/apply")
    public R<String> applyAftersale(@RequestBody AftersaleApplyDTO dto) {
        String aftersaleNo = aftersaleOrderService.applyAftersale(dto);
        return R.success(aftersaleNo);
    }

    @Operation(summary = "售后列表")
    @GetMapping("/list")
    public R<PageResult<AftersaleOrderVO>> getAftersaleList(AftersaleQueryDTO dto) {
        PageResult<AftersaleOrderVO> pageResult = aftersaleOrderService.getUserAftersaleList(dto);
        return R.success(pageResult);
    }

    @Operation(summary = "售后详情")
    @GetMapping("/{id}")
    public R<AftersaleDetailVO> getAftersaleDetail(@PathVariable("id") Long id) {
        AftersaleDetailVO detailVO = aftersaleOrderService.getAftersaleDetail(id);
        return R.success(detailVO);
    }

    @Operation(summary = "取消售后")
    @PostMapping("/cancel/{id}")
    public R<Boolean> cancelAftersale(@PathVariable("id") Long id) {
        Boolean result = aftersaleOrderService.cancelAftersale(id);
        return R.success(result);
    }
}
