package com.fresh.delivery.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.fresh.delivery.entity.DeliveryTrack;
import com.fresh.delivery.mapper.DeliveryTrackMapper;
import com.fresh.delivery.service.DeliveryTrackService;
import com.fresh.delivery.vo.DeliveryTrackVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
public class DeliveryTrackServiceImpl extends ServiceImpl<DeliveryTrackMapper, DeliveryTrack> implements DeliveryTrackService {

    @Override
    public Boolean addTrack(Long deliveryId, Long riderId, BigDecimal longitude, BigDecimal latitude, BigDecimal speed, String direction) {
        DeliveryTrack track = new DeliveryTrack();
        track.setDeliveryId(deliveryId);
        track.setRiderId(riderId);
        track.setLongitude(longitude);
        track.setLatitude(latitude);
        track.setSpeed(speed);
        track.setDirection(direction);
        return this.save(track);
    }

    @Override
    public List<DeliveryTrackVO> getTrackList(Long deliveryId) {
        LambdaQueryWrapper<DeliveryTrack> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(DeliveryTrack::getDeliveryId, deliveryId);
        wrapper.orderByAsc(DeliveryTrack::getCreateTime);
        List<DeliveryTrack> tracks = this.list(wrapper);
        return tracks.stream().map(this::convertToVO).collect(Collectors.toList());
    }

    @Override
    public DeliveryTrackVO getLatestTrack(Long deliveryId) {
        LambdaQueryWrapper<DeliveryTrack> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(DeliveryTrack::getDeliveryId, deliveryId);
        wrapper.orderByDesc(DeliveryTrack::getCreateTime);
        wrapper.last("LIMIT 1");
        DeliveryTrack track = this.getOne(wrapper);
        return track != null ? convertToVO(track) : null;
    }

    private DeliveryTrackVO convertToVO(DeliveryTrack track) {
        DeliveryTrackVO vo = new DeliveryTrackVO();
        BeanUtils.copyProperties(track, vo);
        return vo;
    }
}
