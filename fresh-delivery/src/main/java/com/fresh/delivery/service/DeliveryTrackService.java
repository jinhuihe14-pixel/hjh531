package com.fresh.delivery.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.fresh.delivery.entity.DeliveryTrack;
import com.fresh.delivery.vo.DeliveryTrackVO;

import java.math.BigDecimal;
import java.util.List;

public interface DeliveryTrackService extends IService<DeliveryTrack> {

    Boolean addTrack(Long deliveryId, Long riderId, BigDecimal longitude, BigDecimal latitude, BigDecimal speed, String direction);

    List<DeliveryTrackVO> getTrackList(Long deliveryId);

    DeliveryTrackVO getLatestTrack(Long deliveryId);

}
