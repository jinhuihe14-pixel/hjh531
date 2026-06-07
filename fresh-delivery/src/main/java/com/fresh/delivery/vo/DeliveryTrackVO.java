package com.fresh.delivery.vo;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class DeliveryTrackVO {

    private Long id;

    private BigDecimal longitude;

    private BigDecimal latitude;

    private BigDecimal speed;

    private String direction;

    private LocalDateTime createTime;

}
