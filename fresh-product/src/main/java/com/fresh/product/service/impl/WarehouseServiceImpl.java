package com.fresh.product.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.fresh.common.exception.BusinessException;
import com.fresh.product.entity.Warehouse;
import com.fresh.product.mapper.WarehouseMapper;
import com.fresh.product.service.WarehouseService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class WarehouseServiceImpl extends ServiceImpl<WarehouseMapper, Warehouse> implements WarehouseService {

    @Override
    public List<Warehouse> list(Integer status) {
        LambdaQueryWrapper<Warehouse> wrapper = new LambdaQueryWrapper<>();
        if (status != null) {
            wrapper.eq(Warehouse::getStatus, status);
        }
        wrapper.orderByAsc(Warehouse::getCreateTime);
        return list(wrapper);
    }

    @Override
    public Warehouse getById(Long id) {
        return super.getById(id);
    }

    @Override
    public void add(Warehouse warehouse) {
        if (warehouse.getStatus() == null) {
            warehouse.setStatus(1);
        }
        if (warehouse.getDeliveryRadius() == null) {
            warehouse.setDeliveryRadius(3000);
        }
        save(warehouse);
    }

    @Override
    public void update(Warehouse warehouse) {
        if (warehouse.getId() == null) {
            throw new BusinessException("仓库ID不能为空");
        }
        Warehouse exist = getById(warehouse.getId());
        if (exist == null) {
            throw new BusinessException("仓库不存在");
        }
        updateById(warehouse);
    }

    @Override
    public void delete(Long id) {
        Warehouse warehouse = getById(id);
        if (warehouse == null) {
            throw new BusinessException("仓库不存在");
        }
        removeById(id);
    }
}
