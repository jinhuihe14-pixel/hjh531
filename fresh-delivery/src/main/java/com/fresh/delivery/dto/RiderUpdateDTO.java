package com.fresh.delivery.dto;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class RiderUpdateDTO {

    private String name;

    private String phone;

    private String avatar;

    private String idCard;

    private Integer status;

    private Long currentWarehouseId;

    private BigDecimal currentLongitude;

    private BigDecimal currentLatitude;

    private Integer workStatus;

}
