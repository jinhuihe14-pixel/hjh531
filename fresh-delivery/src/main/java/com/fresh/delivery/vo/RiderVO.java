package com.fresh.delivery.vo;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class RiderVO {

    private Long id;

    private String name;

    private String phone;

    private String avatar;

    private String idCard;

    private Integer status;

    private String statusDesc;

    private BigDecimal currentLongitude;

    private BigDecimal currentLatitude;

    private Long currentWarehouseId;

    private Integer level;

    private Integer totalOrders;

    private BigDecimal rating;

    private Integer workStatus;

    private String workStatusDesc;

    private LocalDateTime lastOnlineTime;

    private LocalDateTime createTime;

}
