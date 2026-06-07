package com.fresh.delivery.vo;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class NearbyRiderVO {

    private Long id;

    private String name;

    private String avatar;

    private Integer level;

    private BigDecimal rating;

    private Integer workStatus;

    private String workStatusDesc;

    private BigDecimal longitude;

    private BigDecimal latitude;

    private BigDecimal distance;

    private Integer currentOrders;

}
