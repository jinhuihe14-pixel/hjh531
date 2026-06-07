package com.fresh.order.vo;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class OrderWarehouseVO {

    private Long id;

    private Long warehouseId;

    private String warehouseName;

    private Integer status;

    private String statusDesc;

    private BigDecimal deliveryFee;

    private Long riderId;

    private LocalDateTime deliveryTime;

}
