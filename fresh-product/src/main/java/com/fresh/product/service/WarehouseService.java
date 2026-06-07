package com.fresh.product.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.fresh.product.entity.Warehouse;

import java.util.List;

public interface WarehouseService extends IService<Warehouse> {

    List<Warehouse> list(Integer status);

    Warehouse getById(Long id);

    void add(Warehouse warehouse);

    void update(Warehouse warehouse);

    void delete(Long id);
}
