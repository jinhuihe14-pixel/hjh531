package com.fresh.warehouse.service.impl;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.fresh.common.exception.BusinessException;
import com.fresh.common.page.PageQuery;
import com.fresh.warehouse.entity.Warehouse;
import com.fresh.warehouse.mapper.WarehouseMapper;
import com.fresh.warehouse.service.WarehouseService;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@Service
public class WarehouseServiceImpl extends ServiceImpl<WarehouseMapper, Warehouse> implements WarehouseService {

    @Override
    public IPage<Warehouse> page(PageQuery pageQuery, String name, Integer status) {
        LambdaQueryWrapper<Warehouse> wrapper = new LambdaQueryWrapper<>();
        if (StrUtil.isNotBlank(name)) {
            wrapper.like(Warehouse::getName, name);
        }
        if (status != null) {
            wrapper.eq(Warehouse::getStatus, status);
        }
        wrapper.orderByAsc(Warehouse::getSort);
        wrapper.orderByDesc(Warehouse::getCreateTime);
        return page(new Page<>(pageQuery.getPageNum(), pageQuery.getPageSize()), wrapper);
    }

    @Override
    public List<Warehouse> list(Integer status) {
        LambdaQueryWrapper<Warehouse> wrapper = new LambdaQueryWrapper<>();
        if (status != null) {
            wrapper.eq(Warehouse::getStatus, status);
        }
        wrapper.orderByAsc(Warehouse::getSort);
        wrapper.orderByDesc(Warehouse::getCreateTime);
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
        if (warehouse.getSort() == null) {
            warehouse.setSort(0);
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

    @Override
    public void updateStatus(Long id, Integer status) {
        Warehouse warehouse = getById(id);
        if (warehouse == null) {
            throw new BusinessException("仓库不存在");
        }
        warehouse.setStatus(status);
        updateById(warehouse);
    }

    @Override
    public List<Warehouse> getNearbyWarehouses(BigDecimal longitude, BigDecimal latitude, Integer radius) {
        List<Warehouse> allWarehouses = list(1);
        return allWarehouses.stream()
                .filter(w -> {
                    if (w.getLongitude() == null || w.getLatitude() == null) {
                        return false;
                    }
                    double distance = calculateDistance(
                            latitude.doubleValue(), longitude.doubleValue(),
                            w.getLatitude().doubleValue(), w.getLongitude().doubleValue()
                    );
                    int effectiveRadius = radius != null ? radius : w.getDeliveryRadius();
                    return distance <= effectiveRadius;
                })
                .sorted((w1, w2) -> {
                    double d1 = calculateDistance(
                            latitude.doubleValue(), longitude.doubleValue(),
                            w1.getLatitude().doubleValue(), w1.getLongitude().doubleValue()
                    );
                    double d2 = calculateDistance(
                            latitude.doubleValue(), longitude.doubleValue(),
                            w2.getLatitude().doubleValue(), w2.getLongitude().doubleValue()
                    );
                    return Double.compare(d1, d2);
                })
                .toList();
    }

    private double calculateDistance(double lat1, double lon1, double lat2, double lon2) {
        double earthRadius = 6371000;
        double dLat = Math.toRadians(lat2 - lat1);
        double dLon = Math.toRadians(lon2 - lon1);
        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2)
                + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
                * Math.sin(dLon / 2) * Math.sin(dLon / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        return earthRadius * c;
    }
}
