package com.fresh.warehouse.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.fresh.common.exception.BusinessException;
import com.fresh.common.page.PageQuery;
import com.fresh.warehouse.entity.WarehouseStaff;
import com.fresh.warehouse.mapper.WarehouseStaffMapper;
import com.fresh.warehouse.service.WarehouseStaffService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class WarehouseStaffServiceImpl extends ServiceImpl<WarehouseStaffMapper, WarehouseStaff> implements WarehouseStaffService {

    @Override
    public IPage<WarehouseStaff> page(PageQuery pageQuery, Long warehouseId, Integer role, Integer status) {
        LambdaQueryWrapper<WarehouseStaff> wrapper = new LambdaQueryWrapper<>();
        if (warehouseId != null) {
            wrapper.eq(WarehouseStaff::getWarehouseId, warehouseId);
        }
        if (role != null) {
            wrapper.eq(WarehouseStaff::getRole, role);
        }
        if (status != null) {
            wrapper.eq(WarehouseStaff::getStatus, status);
        }
        wrapper.orderByDesc(WarehouseStaff::getCreateTime);
        return page(new Page<>(pageQuery.getPageNum(), pageQuery.getPageSize()), wrapper);
    }

    @Override
    public List<WarehouseStaff> listByWarehouseId(Long warehouseId, Integer role) {
        LambdaQueryWrapper<WarehouseStaff> wrapper = new LambdaQueryWrapper<>();
        if (warehouseId != null) {
            wrapper.eq(WarehouseStaff::getWarehouseId, warehouseId);
        }
        if (role != null) {
            wrapper.eq(WarehouseStaff::getRole, role);
        }
        wrapper.eq(WarehouseStaff::getStatus, 1);
        wrapper.orderByAsc(WarehouseStaff::getCreateTime);
        return list(wrapper);
    }

    @Override
    public WarehouseStaff getById(Long id) {
        return super.getById(id);
    }

    @Override
    public void add(WarehouseStaff staff) {
        if (staff.getStatus() == null) {
            staff.setStatus(1);
        }
        save(staff);
    }

    @Override
    public void update(WarehouseStaff staff) {
        if (staff.getId() == null) {
            throw new BusinessException("人员ID不能为空");
        }
        WarehouseStaff exist = getById(staff.getId());
        if (exist == null) {
            throw new BusinessException("人员不存在");
        }
        updateById(staff);
    }

    @Override
    public void delete(Long id) {
        WarehouseStaff staff = getById(id);
        if (staff == null) {
            throw new BusinessException("人员不存在");
        }
        removeById(id);
    }

    @Override
    public void updateStatus(Long id, Integer status) {
        WarehouseStaff staff = getById(id);
        if (staff == null) {
            throw new BusinessException("人员不存在");
        }
        staff.setStatus(status);
        updateById(staff);
    }
}
