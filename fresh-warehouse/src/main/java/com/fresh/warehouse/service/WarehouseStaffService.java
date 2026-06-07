package com.fresh.warehouse.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.fresh.common.page.PageQuery;
import com.fresh.warehouse.entity.WarehouseStaff;

import java.util.List;

public interface WarehouseStaffService extends IService<WarehouseStaff> {

    IPage<WarehouseStaff> page(PageQuery pageQuery, Long warehouseId, Integer role, Integer status);

    List<WarehouseStaff> listByWarehouseId(Long warehouseId, Integer role);

    WarehouseStaff getById(Long id);

    void add(WarehouseStaff staff);

    void update(WarehouseStaff staff);

    void delete(Long id);

    void updateStatus(Long id, Integer status);
}
