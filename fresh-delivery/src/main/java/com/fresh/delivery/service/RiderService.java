package com.fresh.delivery.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.fresh.delivery.dto.RiderAddDTO;
import com.fresh.delivery.dto.RiderQueryDTO;
import com.fresh.delivery.dto.RiderUpdateDTO;
import com.fresh.delivery.entity.Rider;
import com.fresh.delivery.vo.NearbyRiderVO;
import com.fresh.delivery.vo.RiderVO;

import java.math.BigDecimal;
import java.util.List;

public interface RiderService extends IService<Rider> {

    Page<RiderVO> riderPage(RiderQueryDTO dto);

    RiderVO riderDetail(Long id);

    Boolean addRider(RiderAddDTO dto);

    Boolean updateRider(Long id, RiderUpdateDTO dto);

    Boolean deleteRider(Long id);

    Boolean updateStatus(Long id, Integer status);

    Boolean updateWorkStatus(Long id, Integer workStatus);

    List<NearbyRiderVO> nearbyRiders(BigDecimal longitude, BigDecimal latitude, Integer radius, Long warehouseId);

    Boolean updateLocation(Long riderId, BigDecimal longitude, BigDecimal latitude);

}
