package com.fresh.delivery.vo;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class DeliveryDetailVO {

    private Long id;

    private String deliveryNo;

    private String orderNo;

    private Integer status;

    private String statusDesc;

    private BigDecimal deliveryFee;

    private BigDecimal distance;

    private Integer estimateTime;

    private String receiverName;

    private String receiverPhone;

    private String receiverAddress;

    private BigDecimal longitude;

    private BigDecimal latitude;

    private Long riderId;

    private String riderName;

    private String riderPhone;

    private String riderAvatar;

    private BigDecimal riderLongitude;

    private BigDecimal riderLatitude;

    private LocalDateTime pickupTime;

    private LocalDateTime deliveryTime;

    private String remark;

    private LocalDateTime createTime;

    private List<DeliveryTrackVO> tracks;

}
