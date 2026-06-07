package com.fresh.delivery.service.impl;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.fresh.common.exception.BusinessException;
import com.fresh.common.result.ResultCode;
import com.fresh.delivery.dto.RiderAddDTO;
import com.fresh.delivery.dto.RiderQueryDTO;
import com.fresh.delivery.dto.RiderUpdateDTO;
import com.fresh.delivery.entity.Rider;
import com.fresh.delivery.enums.RiderStatus;
import com.fresh.delivery.enums.RiderWorkStatus;
import com.fresh.delivery.mapper.RiderMapper;
import com.fresh.delivery.service.RiderService;
import com.fresh.delivery.vo.NearbyRiderVO;
import com.fresh.delivery.vo.RiderVO;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.data.geo.*;
import org.springframework.data.redis.connection.RedisGeoCommands;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
public class RiderServiceImpl extends ServiceImpl<RiderMapper, Rider> implements RiderService {

    private static final String RIDER_GEO_KEY = "rider:geo:";

    @Resource
    private RedisTemplate<String, Object> redisTemplate;

    @Override
    public Page<RiderVO> riderPage(RiderQueryDTO dto) {
        LambdaQueryWrapper<Rider> wrapper = new LambdaQueryWrapper<>();
        if (StrUtil.isNotBlank(dto.getName())) {
            wrapper.like(Rider::getName, dto.getName());
        }
        if (StrUtil.isNotBlank(dto.getPhone())) {
            wrapper.like(Rider::getPhone, dto.getPhone());
        }
        if (dto.getStatus() != null) {
            wrapper.eq(Rider::getStatus, dto.getStatus());
        }
        if (dto.getWorkStatus() != null) {
            wrapper.eq(Rider::getWorkStatus, dto.getWorkStatus());
        }
        if (dto.getWarehouseId() != null) {
            wrapper.eq(Rider::getCurrentWarehouseId, dto.getWarehouseId());
        }
        wrapper.orderByDesc(Rider::getCreateTime);

        Page<Rider> page = this.page(new Page<>(dto.getPageNum(), dto.getPageSize()), wrapper);
        Page<RiderVO> result = new Page<>(page.getCurrent(), page.getSize(), page.getTotal());
        result.setRecords(page.getRecords().stream().map(this::convertToVO).collect(Collectors.toList()));
        return result;
    }

    @Override
    public RiderVO riderDetail(Long id) {
        Rider rider = this.getById(id);
        if (rider == null) {
            throw new BusinessException(ResultCode.DATA_NOT_EXIST);
        }
        return convertToVO(rider);
    }

    @Override
    public Boolean addRider(RiderAddDTO dto) {
        LambdaQueryWrapper<Rider> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Rider::getPhone, dto.getPhone());
        Long count = this.count(wrapper);
        if (count > 0) {
            throw new BusinessException("手机号已存在");
        }

        Rider rider = new Rider();
        BeanUtils.copyProperties(dto, rider);
        rider.setStatus(RiderStatus.ON_JOB.getCode());
        rider.setWorkStatus(RiderWorkStatus.OFFLINE.getCode());
        rider.setLevel(1);
        rider.setTotalOrders(0);
        rider.setRating(new BigDecimal("5.0"));
        return this.save(rider);
    }

    @Override
    public Boolean updateRider(Long id, RiderUpdateDTO dto) {
        Rider rider = this.getById(id);
        if (rider == null) {
            throw new BusinessException(ResultCode.DATA_NOT_EXIST);
        }

        if (StrUtil.isNotBlank(dto.getPhone()) && !dto.getPhone().equals(rider.getPhone())) {
            LambdaQueryWrapper<Rider> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(Rider::getPhone, dto.getPhone());
            wrapper.ne(Rider::getId, id);
            Long count = this.count(wrapper);
            if (count > 0) {
                throw new BusinessException("手机号已存在");
            }
        }

        Rider update = new Rider();
        BeanUtils.copyProperties(dto, update);
        update.setId(id);
        return this.updateById(update);
    }

    @Override
    public Boolean deleteRider(Long id) {
        Rider rider = this.getById(id);
        if (rider == null) {
            throw new BusinessException(ResultCode.DATA_NOT_EXIST);
        }
        return this.removeById(id);
    }

    @Override
    public Boolean updateStatus(Long id, Integer status) {
        Rider rider = this.getById(id);
        if (rider == null) {
            throw new BusinessException(ResultCode.DATA_NOT_EXIST);
        }
        rider.setStatus(status);
        if (RiderStatus.OFF_JOB.getCode().equals(status) || RiderStatus.REST.getCode().equals(status)) {
            rider.setWorkStatus(RiderWorkStatus.OFFLINE.getCode());
        }
        return this.updateById(rider);
    }

    @Override
    public Boolean updateWorkStatus(Long id, Integer workStatus) {
        Rider rider = this.getById(id);
        if (rider == null) {
            throw new BusinessException(ResultCode.DATA_NOT_EXIST);
        }
        if (!RiderStatus.ON_JOB.getCode().equals(rider.getStatus())) {
            throw new BusinessException("骑手当前状态不可上班");
        }
        rider.setWorkStatus(workStatus);
        if (RiderWorkStatus.OFFLINE.getCode().equals(workStatus)) {
            rider.setLastOnlineTime(LocalDateTime.now());
        }
        return this.updateById(rider);
    }

    @Override
    public List<NearbyRiderVO> nearbyRiders(BigDecimal longitude, BigDecimal latitude, Integer radius, Long warehouseId) {
        LambdaQueryWrapper<Rider> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Rider::getStatus, RiderStatus.ON_JOB.getCode());
        wrapper.eq(Rider::getWorkStatus, RiderWorkStatus.IDLE.getCode());
        if (warehouseId != null) {
            wrapper.eq(Rider::getCurrentWarehouseId, warehouseId);
        }
        List<Rider> riders = this.list(wrapper);

        List<NearbyRiderVO> result = new ArrayList<>();
        for (Rider rider : riders) {
            if (rider.getCurrentLongitude() == null || rider.getCurrentLatitude() == null) {
                continue;
            }
            double distance = calculateDistance(
                    longitude.doubleValue(), latitude.doubleValue(),
                    rider.getCurrentLongitude().doubleValue(), rider.getCurrentLatitude().doubleValue()
            );
            if (radius == null || distance <= radius) {
                NearbyRiderVO vo = new NearbyRiderVO();
                BeanUtils.copyProperties(rider, vo);
                vo.setWorkStatusDesc(RiderWorkStatus.getDesc(rider.getWorkStatus()));
                vo.setDistance(BigDecimal.valueOf(distance));
                vo.setCurrentOrders(0);
                result.add(vo);
            }
        }

        result.sort((a, b) -> a.getDistance().compareTo(b.getDistance()));
        return result;
    }

    @Override
    public Boolean updateLocation(Long riderId, BigDecimal longitude, BigDecimal latitude) {
        Rider rider = this.getById(riderId);
        if (rider == null) {
            throw new BusinessException(ResultCode.DATA_NOT_EXIST);
        }
        rider.setCurrentLongitude(longitude);
        rider.setCurrentLatitude(latitude);
        rider.setLastOnlineTime(LocalDateTime.now());
        return this.updateById(rider);
    }

    private RiderVO convertToVO(Rider rider) {
        RiderVO vo = new RiderVO();
        BeanUtils.copyProperties(rider, vo);
        vo.setStatusDesc(RiderStatus.getDesc(rider.getStatus()));
        vo.setWorkStatusDesc(RiderWorkStatus.getDesc(rider.getWorkStatus()));
        return vo;
    }

    private double calculateDistance(double lon1, double lat1, double lon2, double lat2) {
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
