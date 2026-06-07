package com.fresh.aftersale.service.impl;

import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.fresh.aftersale.dto.AftersaleApplyDTO;
import com.fresh.aftersale.dto.AftersaleApprovalDTO;
import com.fresh.aftersale.dto.AftersaleHandleDTO;
import com.fresh.aftersale.dto.AftersaleItemDTO;
import com.fresh.aftersale.dto.AftersaleQueryDTO;
import com.fresh.aftersale.entity.AftersaleApprovalRecord;
import com.fresh.aftersale.entity.AftersaleItem;
import com.fresh.aftersale.entity.AftersaleLog;
import com.fresh.aftersale.entity.AftersaleOrder;
import com.fresh.aftersale.enums.AftersaleReasonEnum;
import com.fresh.aftersale.enums.AftersaleStatusEnum;
import com.fresh.aftersale.enums.AftersaleTypeEnum;
import com.fresh.aftersale.enums.ApprovalLevelEnum;
import com.fresh.aftersale.feign.OrderFeignClient;
import com.fresh.aftersale.feign.ProductFeignClient;
import com.fresh.aftersale.mapper.AftersaleOrderMapper;
import com.fresh.aftersale.service.AftersaleApprovalRecordService;
import com.fresh.aftersale.service.AftersaleItemService;
import com.fresh.aftersale.service.AftersaleLogService;
import com.fresh.aftersale.service.AftersaleOrderService;
import com.fresh.aftersale.strategy.AftersaleStrategy;
import com.fresh.aftersale.strategy.AftersaleStrategyFactory;
import com.fresh.aftersale.vo.AftersaleDetailVO;
import com.fresh.aftersale.vo.AftersaleItemVO;
import com.fresh.aftersale.vo.AftersaleLogVO;
import com.fresh.aftersale.vo.AftersaleOrderVO;
import com.fresh.aftersale.vo.AftersaleStatisticsVO;
import com.fresh.common.context.UserContext;
import com.fresh.common.exception.BusinessException;
import com.fresh.common.page.PageResult;
import com.fresh.common.result.R;
import com.fresh.common.result.ResultCode;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
public class AftersaleOrderServiceImpl extends ServiceImpl<AftersaleOrderMapper, AftersaleOrder> implements AftersaleOrderService {

    @Resource
    private AftersaleItemService aftersaleItemService;

    @Resource
    private AftersaleLogService aftersaleLogService;

    @Resource
    private AftersaleApprovalRecordService aftersaleApprovalRecordService;

    @Resource
    private AftersaleStrategyFactory aftersaleStrategyFactory;

    @Resource
    private OrderFeignClient orderFeignClient;

    @Resource
    private ProductFeignClient productFeignClient;

    @Value("${aftersale.approval.small-amount:100}")
    private BigDecimal smallAmount;

    @Value("${aftersale.approval.medium-amount:500}")
    private BigDecimal mediumAmount;

    @Value("${aftersale.approval.large-amount:2000}")
    private BigDecimal largeAmount;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public String applyAftersale(AftersaleApplyDTO dto) {
        Long userId = UserContext.getUserId();
        if (userId == null) {
            throw new BusinessException(ResultCode.UNAUTHORIZED);
        }

        AftersaleStrategy strategy = aftersaleStrategyFactory.getStrategy(dto.getType());
        strategy.validate(dto);

        R<java.util.Map<String, Object>> orderResult = orderFeignClient.getOrderByNo(dto.getOrderNo());
        if (orderResult.getCode() != 200 || orderResult.getData() == null) {
            throw new BusinessException(ResultCode.ORDER_NOT_EXIST);
        }

        String aftersaleNo = "AS" + IdUtil.getSnowflakeNextIdStr();

        AftersaleOrder order = new AftersaleOrder();
        order.setAftersaleNo(aftersaleNo);
        order.setOrderNo(dto.getOrderNo());
        order.setUserId(userId);
        order.setType(dto.getType());
        order.setReason(dto.getReason());
        order.setStatus(AftersaleStatusEnum.PENDING_REVIEW.getCode());
        order.setApplyAmount(dto.getApplyAmount());
        order.setRefundAmount(BigDecimal.ZERO);
        order.setApplyTime(LocalDateTime.now());
        order.setRemark(dto.getRemark());

        Integer approvalLevel = calculateApprovalLevel(dto.getApplyAmount());
        order.setApprovalLevel(approvalLevel);

        if (approvalLevel.equals(ApprovalLevelEnum.LEVEL_1.getLevel())) {
            order.setApprovalStatus(1);
        } else {
            order.setApprovalStatus(0);
            order.setStatus(AftersaleStatusEnum.PENDING_APPROVAL.getCode());
        }

        save(order);

        if (dto.getItems() != null && !dto.getItems().isEmpty()) {
            List<AftersaleItem> items = new ArrayList<>();
            for (AftersaleItemDTO itemDTO : dto.getItems()) {
                AftersaleItem item = new AftersaleItem();
                item.setAftersaleId(order.getId());
                item.setSkuId(itemDTO.getSkuId());
                item.setSkuName(itemDTO.getSkuName());
                item.setPrice(itemDTO.getPrice());
                item.setQty(itemDTO.getQty());
                item.setSubtotal(itemDTO.getSubtotal());
                item.setRefundQty(itemDTO.getRefundQty());
                items.add(item);
            }
            aftersaleItemService.saveBatch(items);
        }

        aftersaleLogService.addLog(
                order.getId(),
                1,
                "提交售后申请",
                userId,
                "用户",
                dto.getRemark()
        );

        return aftersaleNo;
    }

    @Override
    public PageResult<AftersaleOrderVO> getUserAftersaleList(AftersaleQueryDTO dto) {
        Long userId = UserContext.getUserId();
        if (userId == null) {
            throw new BusinessException(ResultCode.UNAUTHORIZED);
        }

        LambdaQueryWrapper<AftersaleOrder> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(AftersaleOrder::getUserId, userId);
        if (dto.getType() != null) {
            wrapper.eq(AftersaleOrder::getType, dto.getType());
        }
        if (dto.getStatus() != null) {
            wrapper.eq(AftersaleOrder::getStatus, dto.getStatus());
        }
        if (StrUtil.isNotBlank(dto.getOrderNo())) {
            wrapper.like(AftersaleOrder::getOrderNo, dto.getOrderNo());
        }
        wrapper.orderByDesc(AftersaleOrder::getCreateTime);

        Page<AftersaleOrder> page = new Page<>(dto.getPageNum(), dto.getPageSize());
        page(page, wrapper);

        List<AftersaleOrderVO> voList = page.getRecords().stream()
                .map(this::convertToVO)
                .collect(Collectors.toList());

        return PageResult.of(voList, page.getTotal());
    }

    @Override
    public AftersaleDetailVO getAftersaleDetail(Long id) {
        AftersaleOrder order = getById(id);
        if (order == null) {
            throw new BusinessException(ResultCode.NOT_FOUND.getCode(), "售后单不存在");
        }

        AftersaleDetailVO detailVO = new AftersaleDetailVO();
        BeanUtils.copyProperties(order, detailVO);

        detailVO.setTypeDesc(getTypeDesc(order.getType()));
        detailVO.setReasonDesc(getReasonDesc(order.getReason()));
        detailVO.setStatusDesc(getStatusDesc(order.getStatus()));
        detailVO.setApprovalLevelDesc(getApprovalLevelDesc(order.getApprovalLevel()));

        List<AftersaleItem> items = aftersaleItemService.getByAftersaleId(id);
        List<AftersaleItemVO> itemVOS = items.stream()
                .map(item -> {
                    AftersaleItemVO vo = new AftersaleItemVO();
                    BeanUtils.copyProperties(item, vo);
                    return vo;
                })
                .collect(Collectors.toList());
        detailVO.setItems(itemVOS);

        List<AftersaleLog> logs = aftersaleLogService.getByAftersaleId(id);
        List<AftersaleLogVO> logVOS = logs.stream()
                .map(logItem -> {
                    AftersaleLogVO vo = new AftersaleLogVO();
                    BeanUtils.copyProperties(logItem, vo);
                    return vo;
                })
                .collect(Collectors.toList());
        detailVO.setLogs(logVOS);

        return detailVO;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean cancelAftersale(Long id) {
        Long userId = UserContext.getUserId();
        AftersaleOrder order = getById(id);
        if (order == null) {
            throw new BusinessException(ResultCode.NOT_FOUND.getCode(), "售后单不存在");
        }
        if (!order.getUserId().equals(userId)) {
            throw new BusinessException(ResultCode.FORBIDDEN.getCode(), "无权操作");
        }
        if (!order.getStatus().equals(AftersaleStatusEnum.PENDING_REVIEW.getCode())
                && !order.getStatus().equals(AftersaleStatusEnum.PENDING_APPROVAL.getCode())) {
            throw new BusinessException(ResultCode.FAIL.getCode(), "当前状态不可取消");
        }

        order.setStatus(AftersaleStatusEnum.CANCELLED.getCode());
        updateById(order);

        aftersaleLogService.addLog(
                id,
                2,
                "取消售后申请",
                userId,
                "用户",
                null
        );

        return true;
    }

    @Override
    public PageResult<AftersaleOrderVO> getAdminAftersaleList(AftersaleQueryDTO dto) {
        LambdaQueryWrapper<AftersaleOrder> wrapper = new LambdaQueryWrapper<>();
        if (dto.getType() != null) {
            wrapper.eq(AftersaleOrder::getType, dto.getType());
        }
        if (dto.getStatus() != null) {
            wrapper.eq(AftersaleOrder::getStatus, dto.getStatus());
        }
        if (StrUtil.isNotBlank(dto.getOrderNo())) {
            wrapper.like(AftersaleOrder::getOrderNo, dto.getOrderNo());
        }
        if (StrUtil.isNotBlank(dto.getAftersaleNo())) {
            wrapper.like(AftersaleOrder::getAftersaleNo, dto.getAftersaleNo());
        }
        wrapper.orderByDesc(AftersaleOrder::getCreateTime);

        Page<AftersaleOrder> page = new Page<>(dto.getPageNum(), dto.getPageSize());
        page(page, wrapper);

        List<AftersaleOrderVO> voList = page.getRecords().stream()
                .map(this::convertToVO)
                .collect(Collectors.toList());

        return PageResult.of(voList, page.getTotal());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean reviewAftersale(AftersaleHandleDTO dto) {
        Long handlerId = UserContext.getUserId();
        AftersaleOrder order = getById(dto.getAftersaleId());
        if (order == null) {
            throw new BusinessException(ResultCode.NOT_FOUND.getCode(), "售后单不存在");
        }
        if (!order.getStatus().equals(AftersaleStatusEnum.PENDING_REVIEW.getCode())) {
            throw new BusinessException(ResultCode.FAIL.getCode(), "当前状态不可审核");
        }

        if (dto.getStatus().equals(AftersaleStatusEnum.REJECTED.getCode())) {
            order.setStatus(AftersaleStatusEnum.REJECTED.getCode());
            order.setHandlerId(handlerId);
            order.setHandleTime(LocalDateTime.now());
            order.setRemark(dto.getRemark());
            updateById(order);

            aftersaleLogService.addLog(
                    dto.getAftersaleId(),
                    3,
                    "审核驳回",
                    handlerId,
                    "客服",
                    dto.getRemark()
            );
        } else if (dto.getStatus().equals(AftersaleStatusEnum.PROCESSING.getCode())) {
            if (order.getApprovalLevel() != null && order.getApprovalLevel() > 1) {
                order.setStatus(AftersaleStatusEnum.PENDING_APPROVAL.getCode());
                order.setHandlerId(handlerId);
                order.setHandleTime(LocalDateTime.now());
                updateById(order);

                aftersaleLogService.addLog(
                        dto.getAftersaleId(),
                        4,
                        "审核通过，提交审批",
                        handlerId,
                        "客服",
                        dto.getRemark()
                );
            } else {
                order.setStatus(AftersaleStatusEnum.PROCESSING.getCode());
                order.setHandlerId(handlerId);
                order.setHandleTime(LocalDateTime.now());
                updateById(order);

                aftersaleLogService.addLog(
                        dto.getAftersaleId(),
                        5,
                        "审核通过，开始处理",
                        handlerId,
                        "客服",
                        dto.getRemark()
                );
            }
        }

        return true;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean handleAftersale(AftersaleHandleDTO dto) {
        Long handlerId = UserContext.getUserId();
        AftersaleOrder order = getById(dto.getAftersaleId());
        if (order == null) {
            throw new BusinessException(ResultCode.NOT_FOUND.getCode(), "售后单不存在");
        }
        if (!order.getStatus().equals(AftersaleStatusEnum.PROCESSING.getCode())
                && !order.getStatus().equals(AftersaleStatusEnum.REFUNDING.getCode())) {
            throw new BusinessException(ResultCode.FAIL.getCode(), "当前状态不可处理");
        }

        if (dto.getRefundAmount() != null) {
            order.setRefundAmount(dto.getRefundAmount());
        }

        AftersaleStrategy strategy = aftersaleStrategyFactory.getStrategy(order.getType());
        strategy.processAftersale(order);

        updateById(order);

        aftersaleLogService.addLog(
                dto.getAftersaleId(),
                6,
                "售后处理中",
                handlerId,
                "客服",
                dto.getRemark()
        );

        return true;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean confirmRefund(Long id) {
        Long handlerId = UserContext.getUserId();
        AftersaleOrder order = getById(id);
        if (order == null) {
            throw new BusinessException(ResultCode.NOT_FOUND.getCode(), "售后单不存在");
        }
        if (!order.getStatus().equals(AftersaleStatusEnum.REFUNDING.getCode())
                && !order.getStatus().equals(AftersaleStatusEnum.PROCESSING.getCode())) {
            throw new BusinessException(ResultCode.FAIL.getCode(), "当前状态不可确认退款");
        }

        AftersaleStrategy strategy = aftersaleStrategyFactory.getStrategy(order.getType());
        strategy.restoreStock(order);
        strategy.processRefund(order);

        order.setStatus(AftersaleStatusEnum.COMPLETED.getCode());
        order.setCompleteTime(LocalDateTime.now());
        updateById(order);

        aftersaleLogService.addLog(
                id,
                7,
                "退款完成，售后结案",
                handlerId,
                "客服",
                null
        );

        return true;
    }

    @Override
    public AftersaleStatisticsVO getStatistics() {
        AftersaleStatisticsVO vo = new AftersaleStatisticsVO();

        Long totalCount = count();
        vo.setTotalCount(totalCount);

        LambdaQueryWrapper<AftersaleOrder> pendingWrapper = new LambdaQueryWrapper<>();
        pendingWrapper.eq(AftersaleOrder::getStatus, AftersaleStatusEnum.PENDING_REVIEW.getCode())
                .or().eq(AftersaleOrder::getStatus, AftersaleStatusEnum.PENDING_APPROVAL.getCode());
        vo.setPendingCount(count(pendingWrapper));

        LambdaQueryWrapper<AftersaleOrder> processingWrapper = new LambdaQueryWrapper<>();
        processingWrapper.eq(AftersaleOrder::getStatus, AftersaleStatusEnum.PROCESSING.getCode())
                .or().eq(AftersaleOrder::getStatus, AftersaleStatusEnum.REFUNDING.getCode());
        vo.setProcessingCount(count(processingWrapper));

        LambdaQueryWrapper<AftersaleOrder> completedWrapper = new LambdaQueryWrapper<>();
        completedWrapper.eq(AftersaleOrder::getStatus, AftersaleStatusEnum.COMPLETED.getCode());
        vo.setCompletedCount(count(completedWrapper));

        LambdaQueryWrapper<AftersaleOrder> rejectedWrapper = new LambdaQueryWrapper<>();
        rejectedWrapper.eq(AftersaleOrder::getStatus, AftersaleStatusEnum.REJECTED.getCode());
        vo.setRejectedCount(count(rejectedWrapper));

        List<AftersaleOrder> completedOrders = list(completedWrapper);
        BigDecimal totalRefundAmount = completedOrders.stream()
                .map(AftersaleOrder::getRefundAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        vo.setTotalRefundAmount(totalRefundAmount);

        return vo;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean approvalAftersale(AftersaleApprovalDTO dto) {
        Long approverId = UserContext.getUserId();
        AftersaleOrder order = getById(dto.getAftersaleId());
        if (order == null) {
            throw new BusinessException(ResultCode.NOT_FOUND.getCode(), "售后单不存在");
        }
        if (!order.getStatus().equals(AftersaleStatusEnum.PENDING_APPROVAL.getCode())) {
            throw new BusinessException(ResultCode.FAIL.getCode(), "当前状态不可审批");
        }

        Integer currentLevel = order.getApprovalLevel();
        String approverName = "审批人";

        if (dto.getPass()) {
            if (currentLevel.equals(ApprovalLevelEnum.LEVEL_2.getLevel())) {
                aftersaleApprovalRecordService.addRecord(
                        dto.getAftersaleId(),
                        currentLevel,
                        1,
                        approverId,
                        approverName,
                        dto.getRemark()
                );
                order.setStatus(AftersaleStatusEnum.PROCESSING.getCode());
                order.setApprovalStatus(1);
                updateById(order);

                aftersaleLogService.addLog(
                        dto.getAftersaleId(),
                        8,
                        "主管审批通过",
                        approverId,
                        approverName,
                        dto.getRemark()
                );
            } else if (currentLevel.equals(ApprovalLevelEnum.LEVEL_3.getLevel())) {
                aftersaleApprovalRecordService.addRecord(
                        dto.getAftersaleId(),
                        currentLevel,
                        1,
                        approverId,
                        approverName,
                        dto.getRemark()
                );
                order.setStatus(AftersaleStatusEnum.PROCESSING.getCode());
                order.setApprovalStatus(1);
                updateById(order);

                aftersaleLogService.addLog(
                        dto.getAftersaleId(),
                        9,
                        "经理审批通过",
                        approverId,
                        approverName,
                        dto.getRemark()
                );
            }
        } else {
            aftersaleApprovalRecordService.addRecord(
                    dto.getAftersaleId(),
                    currentLevel,
                    0,
                    approverId,
                    approverName,
                    dto.getRemark()
            );
            order.setStatus(AftersaleStatusEnum.REJECTED.getCode());
            order.setApprovalStatus(2);
            updateById(order);

            aftersaleLogService.addLog(
                    dto.getAftersaleId(),
                    10,
                    "审批驳回",
                    approverId,
                    approverName,
                    dto.getRemark()
            );
        }

        return true;
    }

    private Integer calculateApprovalLevel(BigDecimal amount) {
        if (amount == null) {
            return ApprovalLevelEnum.LEVEL_1.getLevel();
        }
        if (amount.compareTo(smallAmount) <= 0) {
            return ApprovalLevelEnum.LEVEL_1.getLevel();
        } else if (amount.compareTo(mediumAmount) <= 0) {
            return ApprovalLevelEnum.LEVEL_2.getLevel();
        } else {
            return ApprovalLevelEnum.LEVEL_3.getLevel();
        }
    }

    private AftersaleOrderVO convertToVO(AftersaleOrder order) {
        AftersaleOrderVO vo = new AftersaleOrderVO();
        BeanUtils.copyProperties(order, vo);
        vo.setTypeDesc(getTypeDesc(order.getType()));
        vo.setReasonDesc(getReasonDesc(order.getReason()));
        vo.setStatusDesc(getStatusDesc(order.getStatus()));
        return vo;
    }

    private String getTypeDesc(Integer type) {
        if (type == null) {
            return "";
        }
        for (AftersaleTypeEnum typeEnum : AftersaleTypeEnum.values()) {
            if (typeEnum.getCode().equals(type)) {
                return typeEnum.getDesc();
            }
        }
        return "";
    }

    private String getReasonDesc(Integer reason) {
        if (reason == null) {
            return "";
        }
        for (AftersaleReasonEnum reasonEnum : AftersaleReasonEnum.values()) {
            if (reasonEnum.getCode().equals(reason)) {
                return reasonEnum.getDesc();
            }
        }
        return "";
    }

    private String getStatusDesc(Integer status) {
        if (status == null) {
            return "";
        }
        for (AftersaleStatusEnum statusEnum : AftersaleStatusEnum.values()) {
            if (statusEnum.getCode().equals(status)) {
                return statusEnum.getDesc();
            }
        }
        return "";
    }

    private String getApprovalLevelDesc(Integer level) {
        if (level == null) {
            return "";
        }
        for (ApprovalLevelEnum levelEnum : ApprovalLevelEnum.values()) {
            if (levelEnum.getLevel().equals(level)) {
                return levelEnum.getDesc();
            }
        }
        return "";
    }
}
