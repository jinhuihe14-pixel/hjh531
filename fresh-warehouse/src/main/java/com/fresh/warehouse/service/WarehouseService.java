package com.fresh.warehouse.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.fresh.common.page.PageQuery;
import com.fresh.warehouse.entity.Warehouse;

import java.math.BigDecimal;
import java.util.List;

public interface WarehouseService extends IService<Warehouse> {

    IPage<Warehouse> page(PageQuery pageQuery, String name, Integer status);

    List<Warehouse> list(Integer status);

    Warehouse getById(Long id);

    void add(Warehouse warehouse);

    void update(Warehouse warehouse);

    void delete(Long id);

    void updateStatus(Long id, Integer status);

    List<Warehouse> getNearbyWarehouses(BigDecimal longitude, BigDecimal latitude, Integer radius);
}
