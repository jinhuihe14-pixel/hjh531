package com.fresh.warehouse.service.impl;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.fresh.common.context.UserContext;
import com.fresh.common.exception.BusinessException;
import com.fresh.common.page.PageQuery;
import com.fresh.common.result.R;
import com.fresh.warehouse.dto.StockTransferCreateDTO;
import com.fresh.warehouse.dto.StockTransferItemDTO;
import com.fresh.warehouse.dto.StockTransferQueryDTO;
import com.fresh.warehouse.entity.StockTransfer;
import com.fresh.warehouse.entity.StockTransferItem;
import com.fresh.warehouse.entity.Warehouse;
import com.fresh.warehouse.enums.StockTransferStatus;
import com.fresh.warehouse.feign.ProductFeignClient;
import com.fresh.warehouse.mapper.StockTransferMapper;
import com.fresh.warehouse.service.StockTransferItemService;
import com.fresh.warehouse.service.StockTransferService;
import com.fresh.warehouse.service.WarehouseService;
import com.fresh.warehouse.vo.StockTransferDetailVO;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
public class StockTransferServiceImpl extends ServiceImpl<StockTransferMapper, StockTransfer> implements StockTransferService {

    @Resource
    private StockTransferItemService stockTransferItemService;

    @Resource
    private WarehouseService warehouseService;

    @Resource
    private ProductFeignClient productFeignClient;

    @Override
    public IPage<StockTransfer> page(PageQuery pageQuery, StockTransferQueryDTO queryDTO) {
        LambdaQueryWrapper<StockTransfer> wrapper = new LambdaQueryWrapper<>();
        if (StrUtil.isNotBlank(queryDTO.getTransferNo())) {
            wrapper.like(StockTransfer::getTransferNo, queryDTO.getTransferNo());
        }
        if (queryDTO.getFromWarehouseId() != null) {
            wrapper.eq(StockTransfer::getFromWarehouseId, queryDTO.getFromWarehouseId());
        }
        if (queryDTO.getToWarehouseId() != null) {
            wrapper.eq(StockTransfer::getToWarehouseId, queryDTO.getToWarehouseId());
        }
        if (queryDTO.getStatus() != null) {
            wrapper.eq(StockTransfer::getStatus, queryDTO.getStatus());
        }
        wrapper.orderByDesc(StockTransfer::getCreateTime);
        return page(new Page<>(pageQuery.getPageNum(), pageQuery.getPageSize()), wrapper);
    }

    @Override
    public StockTransferDetailVO getDetailById(Long id) {
        StockTransfer transfer = getById(id);
        if (transfer == null) {
            throw new BusinessException("调拨单不存在");
        }
        StockTransferDetailVO vo = new StockTransferDetailVO();
        org.springframework.beans.BeanUtils.copyProperties(transfer, vo);

        Warehouse fromWarehouse = warehouseService.getById(transfer.getFromWarehouseId());
        if (fromWarehouse != null) {
            vo.setFromWarehouseName(fromWarehouse.getName());
        }
        Warehouse toWarehouse = warehouseService.getById(transfer.getToWarehouseId());
        if (toWarehouse != null) {
            vo.setToWarehouseName(toWarehouse.getName());
        }

        List<StockTransferItem> items = stockTransferItemService.listByTransferId(id);
        vo.setItems(items);

        return vo;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void createTransfer(StockTransferCreateDTO dto) {
        if (dto.getFromWarehouseId().equals(dto.getToWarehouseId())) {
            throw new BusinessException("调出仓和调入仓不能相同");
        }

        Warehouse fromWarehouse = warehouseService.getById(dto.getFromWarehouseId());
        if (fromWarehouse == null) {
            throw new BusinessException("调出仓不存在");
        }
        if (fromWarehouse.getStatus() != 1) {
            throw new BusinessException("调出仓已停用");
        }

        Warehouse toWarehouse = warehouseService.getById(dto.getToWarehouseId());
        if (toWarehouse == null) {
            throw new BusinessException("调入仓不存在");
        }
        if (toWarehouse.getStatus() != 1) {
            throw new BusinessException("调入仓已停用");
        }

        String transferNo = "DB" + System.currentTimeMillis() + UUID.randomUUID().toString().substring(0, 4).toUpperCase();

        StockTransfer transfer = new StockTransfer();
        transfer.setTransferNo(transferNo);
        transfer.setFromWarehouseId(dto.getFromWarehouseId());
        transfer.setToWarehouseId(dto.getToWarehouseId());
        transfer.setStatus(StockTransferStatus.PENDING.getCode());
        transfer.setApplyUserId(UserContext.getUserId());
        transfer.setRemark(dto.getRemark());
        save(transfer);

        List<StockTransferItem> items = new ArrayList<>();
        for (StockTransferItemDTO itemDTO : dto.getItems()) {
            if (itemDTO.getQty() == null || itemDTO.getQty() <= 0) {
                throw new BusinessException("调拨数量必须大于0");
            }
            StockTransferItem item = new StockTransferItem();
            item.setTransferId(transfer.getId());
            item.setTransferNo(transferNo);
            item.setSkuId(itemDTO.getSkuId());
            item.setQty(itemDTO.getQty());
            items.add(item);
        }
        stockTransferItemService.saveBatch(items);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void approve(Long id, Boolean pass, String remark) {
        StockTransfer transfer = getById(id);
        if (transfer == null) {
            throw new BusinessException("调拨单不存在");
        }
        if (!StockTransferStatus.PENDING.getCode().equals(transfer.getStatus())) {
            throw new BusinessException("只有待审批状态的调拨单才能审批");
        }

        transfer.setApproveUserId(UserContext.getUserId());
        transfer.setApproveTime(LocalDateTime.now());

        if (Boolean.TRUE.equals(pass)) {
            transfer.setStatus(StockTransferStatus.APPROVED.getCode());
        } else {
            transfer.setStatus(StockTransferStatus.REJECTED.getCode());
            if (StrUtil.isNotBlank(remark)) {
                transfer.setRemark(transfer.getRemark() == null ? remark : transfer.getRemark() + "；驳回原因：" + remark);
            }
        }

        updateById(transfer);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void outbound(Long id) {
        StockTransfer transfer = getById(id);
        if (transfer == null) {
            throw new BusinessException("调拨单不存在");
        }
        if (!StockTransferStatus.APPROVED.getCode().equals(transfer.getStatus())) {
            throw new BusinessException("只有已批准状态的调拨单才能出库");
        }

        List<StockTransferItem> items = stockTransferItemService.listByTransferId(id);
        if (items.isEmpty()) {
            throw new BusinessException("调拨明细不能为空");
        }

        for (StockTransferItem item : items) {
            R<Boolean> result = productFeignClient.stockOut(
                    transfer.getFromWarehouseId(),
                    item.getSkuId(),
                    item.getQty(),
                    transfer.getTransferNo(),
                    "调拨出库"
            );
            if (result.getCode() != 200 || result.getData() == null || !result.getData()) {
                throw new BusinessException("库存出库失败，SKU ID：" + item.getSkuId());
            }
        }

        transfer.setStatus(StockTransferStatus.TRANSFERRING.getCode());
        updateById(transfer);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void inbound(Long id) {
        StockTransfer transfer = getById(id);
        if (transfer == null) {
            throw new BusinessException("调拨单不存在");
        }
        if (!StockTransferStatus.TRANSFERRING.getCode().equals(transfer.getStatus())) {
            throw new BusinessException("只有调拨中状态的调拨单才能入库");
        }

        List<StockTransferItem> items = stockTransferItemService.listByTransferId(id);
        if (items.isEmpty()) {
            throw new BusinessException("调拨明细不能为空");
        }

        for (StockTransferItem item : items) {
            R<Boolean> result = productFeignClient.stockIn(
                    transfer.getToWarehouseId(),
                    item.getSkuId(),
                    item.getQty(),
                    transfer.getTransferNo(),
                    "调拨入库"
            );
            if (result.getCode() != 200 || result.getData() == null || !result.getData()) {
                throw new BusinessException("库存入库失败，SKU ID：" + item.getSkuId());
            }
        }

        transfer.setStatus(StockTransferStatus.COMPLETED.getCode());
        updateById(transfer);
    }
}
