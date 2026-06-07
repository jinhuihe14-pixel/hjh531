package com.fresh.delivery.vo;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class DeliveryOrderVO {

    private Long id;

    private String deliveryNo;

    private String orderNo;

    private Long orderWarehouseId;

    private Long warehouseId;

    private Long riderId;

    private String riderName;

    private String riderPhone;

    private String receiverName;

    private String receiverPhone;

    private String receiverAddress;

    private BigDecimal longitude;

    private BigDecimal latitude;

    private Integer status;

    private String statusDesc;

    private BigDecimal deliveryFee;

    private BigDecimal distance;

    private Integer estimateTime;

    private LocalDateTime pickupTime;

    private LocalDateTime deliveryTime;

    private String cancelReason;

    private String remark;

    private LocalDateTime createTime;

}
